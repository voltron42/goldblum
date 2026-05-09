(ns goldblum.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.util.response :as response]
            [reitit.ring :as ring]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [clojure.tools.logging :as log])
  (:gen-class))

;; In-memory data store (demo only - resets on server restart)
(def users (atom [{:id 1 :name "Alice" :email "alice@example.com"}
                   {:id 2 :name "Bob" :email "bob@example.com"}]))

(def posts (atom [{:id 1 :userId 1 :title "Hello World" :content "Welcome to Goldblum"}
                  {:id 2 :userId 2 :title "Getting Started" :content "Let's deploy to fly.io"}]))

;; API Routes with Swagger documentation
(def routes
  [["/swagger.json"
    {:get {:no-doc true
           :handler (swagger/create-swagger-handler)}}]
   
   ["/api"
    {:swagger {:tags ["API"]}}
    
    ["/hello"
     {:get {:summary "Simple hello endpoint"
            :responses {200 {:body {:message string?}}}
            :handler (fn [request]
                       (response/response {:message "Hello from Goldblum!"}))}}]
    
    ["/users"
     {:get {:summary "Get all users"
            :responses {200 {:body [{:id int? :name string? :email string?}]}}
            :handler (fn [request]
                       (response/response @users))}
      :post {:summary "Create a new user"
             :parameters {:body {:name string? :email string?}}
             :responses {201 {:body {:id int? :name string? :email string?}}}
             :handler (fn [request]
                        (let [user (:body request)
                              id (inc (count @users))
                              new-user (assoc user :id id)]
                          (swap! users conj new-user)
                          (response/created (str "/api/users/" id) new-user)))}}]
    
    ["/users/:id"
     {:get {:summary "Get a specific user by ID"
            :parameters {:path {:id int?}}
            :responses {200 {:body {:id int? :name string? :email string?}}}
            :handler (fn [request]
                       (let [id (Integer/parseInt (get-in request [:path-params :id]))
                             user (first (filter #(= (:id %) id) @users))]
                         (if user
                           (response/response user)
                           (response/not-found {:error "User not found"}))))}
      :put {:summary "Update a user"
            :parameters {:path {:id int?}
                         :body {:name string? :email string?}}
            :responses {200 {:body {:id int? :name string? :email string?}}}
            :handler (fn [request]
                       (let [id (Integer/parseInt (get-in request [:path-params :id]))
                             updates (:body request)
                             user (first (filter #(= (:id %) id) @users))]
                         (if user
                           (let [updated (merge user updates)]
                             (swap! users (fn [u] (mapv #(if (= (:id %) id) updated %) u)))
                             (response/response updated))
                           (response/not-found {:error "User not found"}))))}
      :delete {:summary "Delete a user"
               :parameters {:path {:id int?}}
               :responses {204 {}}
               :handler (fn [request]
                          (let [id (Integer/parseInt (get-in request [:path-params :id]))]
                            (swap! users (fn [u] (filterv #(not= (:id %) id) u)))
                            (response/response {:deleted id})))}}]
    
    ["/posts"
     {:get {:summary "Get all posts"
            :responses {200 {:body [{:id int? :userId int? :title string? :content string?}]}}
            :handler (fn [request]
                       (response/response @posts))}
      :post {:summary "Create a new post"
             :parameters {:body {:userId int? :title string? :content string?}}
             :responses {201 {:body {:id int? :userId int? :title string? :content string?}}}
             :handler (fn [request]
                        (let [post (:body request)
                              id (inc (count @posts))
                              new-post (assoc post :id id)]
                          (swap! posts conj new-post)
                          (response/created (str "/api/posts/" id) new-post)))}}]
    
    ["/posts/:id"
     {:get {:summary "Get a specific post by ID"
            :parameters {:path {:id int?}}
            :responses {200 {:body {:id int? :userId int? :title string? :content string?}}}
            :handler (fn [request]
                       (let [id (Integer/parseInt (get-in request [:path-params :id]))
                             post (first (filter #(= (:id %) id) @posts))]
                         (if post
                           (response/response post)
                           (response/not-found {:error "Post not found"}))))}
      :delete {:summary "Delete a post"
               :parameters {:path {:id int?}}
               :responses {204 {}}
               :handler (fn [request]
                          (let [id (Integer/parseInt (get-in request [:path-params :id]))]
                            (swap! posts (fn [p] (filterv #(not= (:id %) id) p)))
                            (response/response {:deleted id})))}}]
    
    ["/health"
     {:get {:summary "Health check"
            :responses {200 {:body {:status string?}}}
            :handler (fn [request]
                       (response/response {:status "ok"}))}}]]])

;; Create router with Swagger UI and static file serving
(def app
  (ring/ring-handler
    (ring/router
      routes
      {:data {:swagger {:ui {:path "/swagger-ui"}
                        :spec "/swagger.json"}}})
    (ring/routes
      (swagger-ui/create-swagger-ui-handler {:path "/swagger-ui" :url "/swagger.json"})
      (ring/redirect-trailing-slash-handler)
      (ring/create-default-handler))))

;; HTTP handler with middleware (JSON, CORS, static files)
(def handler
  (-> app
      wrap-not-modified
      wrap-json-response
      (wrap-json-body {:keywords? true})
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :delete :options]
                 :access-control-allow-headers ["Content-Type" "Authorization"])
      (wrap-resource "public")))

(defn -main
  "Start Jetty server on PORT (default: 8080)"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "8080"))]
    (log/info (str "Starting Goldblum on http://0.0.0.0:" port))
    (jetty/run-jetty handler {:host "0.0.0.0" :port port :join? false})))
