(ns goldblum.test-utils
  "Shared test utilities and helpers for common testing patterns."
  (:require [ring.mock.request :as mock]
            [cheshire.core :as json]))

;; HTTP Request Helpers

(defn make-request
  "Create a mock HTTP request with optional body and content type."
  ([method url]
   (make-request method url nil nil))
  ([method url body]
   (make-request method url body "application/json"))
  ([method url body content-type]
   (cond-> (mock/request method url)
     content-type (mock/content-type content-type)
     body (mock/body (if (string? body) body (json/generate-string body))))))

(defn get-request [url]
  "Create a GET request."
  (mock/request :get url))

(defn post-request [url body]
  "Create a POST request with JSON body."
  (make-request :post url body))

(defn put-request [url body]
  "Create a PUT request with JSON body."
  (make-request :put url body))

(defn delete-request [url]
  "Create a DELETE request."
  (mock/request :delete url))

;; Response Helpers

(defn parse-json [response]
  "Parse JSON response body."
  (when (:body response)
    (json/parse-string (:body response) true)))

(defn get-status [response]
  "Extract HTTP status code from response."
  (:status response))

(defn get-body [response]
  "Extract and parse body from response."
  (parse-json response))

(defn is-success? [response]
  "Check if response is a 2xx status code."
  (let [status (get-status response)]
    (and (>= status 200) (< status 300))))

(defn is-error? [response]
  "Check if response is a 4xx or 5xx status code."
  (let [status (get-status response)]
    (or (>= status 400) (>= status 500))))

(defn is-not-found? [response]
  "Check if response is 404."
  (= 404 (get-status response)))

(defn is-created? [response]
  "Check if response is 201 Created."
  (= 201 (get-status response)))

;; Assertion Helpers

(defn assert-status
  "Assert response has expected status code."
  [response expected-status]
  (= expected-status (get-status response)))

(defn assert-body-equals
  "Assert response body equals expected value."
  [response expected-body]
  (= expected-body (get-body response)))

(defn assert-contains-keys
  "Assert response body contains all keys."
  [response keys-seq]
  (let [body (get-body response)]
    (every? (fn [key] (contains? body key)) keys-seq)))

(defn assert-json-response
  "Assert response has JSON content type."
  [response]
  (let [headers (:headers response)]
    (or (contains? headers "Content-Type")
        (contains? headers "content-type"))))

;; Data Factory Helpers

(defn user-fixture
  "Create a test user with optional overrides."
  [& {:keys [id name email] :or {id 1 name "Test User" email "test@example.com"}}]
  {:id id :name name :email email})

(defn post-fixture
  "Create a test post with optional overrides."
  [& {:keys [id userId title content] 
      :or {id 1 userId 1 title "Test Post" content "Test content"}}]
  {:id id :userId userId :title title :content content})

;; Batch Operation Helpers

(defn create-users
  "Create multiple users via API."
  [handler count]
  (mapv (fn [i]
          (let [response (handler
                          (post-request "/api/users"
                                       {:name (str "User" i) 
                                        :email (str "user" i "@example.com")}))]
            (get-body response)))
        (range count)))

(defn create-posts
  "Create multiple posts via API."
  [handler count user-id]
  (mapv (fn [i]
          (let [response (handler
                          (post-request "/api/posts"
                                       {:userId user-id
                                        :title (str "Post" i)
                                        :content (str "Content" i)}))]
            (get-body response)))
        (range count)))
