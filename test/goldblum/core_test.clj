(ns goldblum.core-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [goldblum.core :as core]))

;; Test fixtures to reset state before each test
(def reset-state
  (fn [f]
    (reset! core/users [{:id 1 :name "Alice" :email "alice@example.com"}
                        {:id 2 :name "Bob" :email "bob@example.com"}])
    (reset! core/posts [{:id 1 :userId 1 :title "Hello World" :content "Welcome to Goldblum"}
                        {:id 2 :userId 2 :title "Getting Started" :content "Let's deploy to fly.io"}])
    (f)))

(use-fixtures :each reset-state)
;; Helper function to parse JSON response
(defn parse-response [response]
  (when (:body response)
    (json/parse-string (:body response) true)))

;; Health Check Tests
(deftest health-check
  (testing "GET /api/health returns 200 with ok status"
    (let [response (core/handler (mock/request :get "/api/health"))]
      (is (= 200 (:status response)))
      (is (= {:status "ok"} (parse-response response))))))

;; Hello Endpoint Tests
(deftest hello-endpoint
  (testing "GET /api/hello returns 200 with message"
    (let [response (core/handler (mock/request :get "/api/hello"))]
      (is (= 200 (:status response)))
      (is (= "Hello from Goldblum!" (:message (parse-response response)))))))

;; User List Tests
(deftest get-all-users
  (testing "GET /api/users returns 200 with list of users"
    (let [response (core/handler (mock/request :get "/api/users"))
          users (parse-response response)]
      (is (= 200 (:status response)))
      (is (= 2 (count users)))
      (is (= "Alice" (:name (first users))))
      (is (= "Bob" (:name (second users))))))

  (testing "GET /api/users returns empty list after deleting all"
    (swap! core/users empty)
    (let [response (core/handler (mock/request :get "/api/users"))
          users (parse-response response)]
      (is (= 200 (:status response)))
      (is (= 0 (count users))))))

;; Create User Tests
(deftest create-user
  (testing "POST /api/users creates a new user"
    (let [body {:name "Charlie" :email "charlie@example.com"}
          response (core/handler
                     (-> (mock/request :post "/api/users")
                         (mock/content-type "application/json")
                         (mock/body (json/generate-string body))))
          created-user (parse-response response)]
      (is (= 201 (:status response)))
      (is (= 3 (:id created-user)))
      (is (= "Charlie" (:name created-user)))
      (is (= "charlie@example.com" (:email created-user)))))

  (testing "New user is added to the store"
    (reset! core/users [{:id 1 :name "Alice" :email "alice@example.com"}
                        {:id 2 :name "Bob" :email "bob@example.com"}])
    (let [body {:name "Diana" :email "diana@example.com"}
          _ (core/handler
              (-> (mock/request :post "/api/users")
                  (mock/content-type "application/json")
                  (mock/body (json/generate-string body))))
          users @core/users]
      (is (= 3 (count users)))
      (is (= "Diana" (:name (last users)))))))

;; Get User by ID Tests
(deftest get-user-by-id
  (testing "GET /api/users/1 returns user"
    (let [response (core/handler (mock/request :get "/api/users/1"))
          user (parse-response response)]
      (is (= 200 (:status response)))
      (is (= 1 (:id user)))
      (is (= "Alice" (:name user)))))

  (testing "GET /api/users/999 returns 404"
    (let [response (core/handler (mock/request :get "/api/users/999"))]
      (is (= 404 (:status response)))
      (is (= {:error "User not found"} (parse-response response))))))

;; Update User Tests
(deftest update-user
  (testing "PUT /api/users/1 updates user"
    (let [body {:name "Alice Updated" :email "alice.new@example.com"}
          response (core/handler
                     (-> (mock/request :put "/api/users/1")
                         (mock/content-type "application/json")
                         (mock/body (json/generate-string body))))
          updated-user (parse-response response)]
      (is (= 200 (:status response)))
      (is (= 1 (:id updated-user)))
      (is (= "Alice Updated" (:name updated-user)))
      (is (= "alice.new@example.com" (:email updated-user)))))

  (testing "Updated user persists in store"
    (let [body {:name "Bob Updated" :email "bob.new@example.com"}
          _ (core/handler
              (-> (mock/request :put "/api/users/2")
                  (mock/content-type "application/json")
                  (mock/body (json/generate-string body))))
          user (first (filter #(= (:id %) 2) @core/users))]
      (is (= "Bob Updated" (:name user)))
      (is (= "bob.new@example.com" (:email user)))))

  (testing "PUT /api/users/999 returns 404"
    (let [body {:name "Nonexistent" :email "none@example.com"}
          response (core/handler
                     (-> (mock/request :put "/api/users/999")
                         (mock/content-type "application/json")
                         (mock/body (json/generate-string body))))]
      (is (= 404 (:status response))))))

;; Delete User Tests
(deftest delete-user
  (testing "DELETE /api/users/1 removes user"
    (let [response (core/handler (mock/request :delete "/api/users/1"))]
      (is (= 200 (:status response)))
      (is (= {:deleted 1} (parse-response response)))
      (is (= 1 (count @core/users)))))

  (testing "Deleted user no longer exists"
    (core/handler (mock/request :delete "/api/users/2"))
    (let [response (core/handler (mock/request :get "/api/users/2"))]
      (is (= 404 (:status response)))))

  (testing "DELETE /api/users/999 succeeds (idempotent)"
    (let [response (core/handler (mock/request :delete "/api/users/999"))]
      (is (= 200 (:status response))))))

;; Get All Posts Tests
(deftest get-all-posts
  (testing "GET /api/posts returns 200 with list of posts"
    (let [response (core/handler (mock/request :get "/api/posts"))
          posts (parse-response response)]
      (is (= 200 (:status response)))
      (is (= 2 (count posts)))
      (is (= "Hello World" (:title (first posts))))
      (is (= "Getting Started" (:title (second posts))))))

  (testing "GET /api/posts includes userId"
    (let [response (core/handler (mock/request :get "/api/posts"))
          posts (parse-response response)]
      (is (= 1 (:userId (first posts))))
      (is (= 2 (:userId (second posts)))))))

;; Create Post Tests
(deftest create-post
  (testing "POST /api/posts creates a new post"
    (let [body {:userId 1 :title "New Post" :content "This is a new post"}
          response (core/handler
                     (-> (mock/request :post "/api/posts")
                         (mock/content-type "application/json")
                         (mock/body (json/generate-string body))))
          created-post (parse-response response)]
      (is (= 201 (:status response)))
      (is (= 3 (:id created-post)))
      (is (= "New Post" (:title created-post)))
      (is (= 1 (:userId created-post)))))

  (testing "New post is added to the store"
    (reset! core/posts [{:id 1 :userId 1 :title "Hello World" :content "Welcome to Goldblum"}
                        {:id 2 :userId 2 :title "Getting Started" :content "Let's deploy to fly.io"}])
    (let [body {:userId 2 :title "Another Post" :content "More content"}
          _ (core/handler
              (-> (mock/request :post "/api/posts")
                  (mock/content-type "application/json")
                  (mock/body (json/generate-string body))))
          posts @core/posts]
      (is (= 3 (count posts)))
      (is (= "Another Post" (:title (last posts)))))))

;; Get Post by ID Tests
(deftest get-post-by-id
  (testing "GET /api/posts/1 returns post"
    (let [response (core/handler (mock/request :get "/api/posts/1"))
          post (parse-response response)]
      (is (= 200 (:status response)))
      (is (= 1 (:id post)))
      (is (= "Hello World" (:title post)))))

  (testing "GET /api/posts/999 returns 404"
    (let [response (core/handler (mock/request :get "/api/posts/999"))]
      (is (= 404 (:status response)))
      (is (= {:error "Post not found"} (parse-response response))))))

;; Delete Post Tests
(deftest delete-post
  (testing "DELETE /api/posts/1 removes post"
    (let [response (core/handler (mock/request :delete "/api/posts/1"))]
      (is (= 200 (:status response)))
      (is (= {:deleted 1} (parse-response response)))
      (is (= 1 (count @core/posts)))))

  (testing "Deleted post no longer exists"
    (core/handler (mock/request :delete "/api/posts/2"))
    (let [response (core/handler (mock/request :get "/api/posts/2"))]
      (is (= 404 (:status response)))))

  (testing "DELETE /api/posts/999 succeeds (idempotent)"
    (let [response (core/handler (mock/request :delete "/api/posts/999"))]
      (is (= 200 (:status response))))))

;; Swagger Endpoint Tests
(deftest swagger-endpoints
  (testing "GET /swagger.json returns 200"
    (let [response (core/handler (mock/request :get "/swagger.json"))]
      (is (= 200 (:status response)))))

  (testing "GET /swagger-ui returns 200"
    (let [response (core/handler (mock/request :get "/swagger-ui"))]
      (is (or (= 200 (:status response))
              (= 302 (:status response)))))))

;; HTTP Method Tests
(deftest http-methods
  (testing "POST to GET-only endpoint fails"
    (let [response (core/handler (mock/request :post "/api/hello"))]
      (is (or (= 404 (:status response))
              (= 405 (:status response))))))

  (testing "PUT to read-only endpoint fails"
    (let [response (core/handler (mock/request :put "/api/hello"))]
      (is (or (= 404 (:status response))
              (= 405 (:status response)))))))

;; Content Type Tests
(deftest content-type-handling
  (testing "POST with JSON content type succeeds"
    (let [body {:name "Test" :email "test@example.com"}
          response (core/handler
                     (-> (mock/request :post "/api/users")
                         (mock/content-type "application/json")
                         (mock/body (json/generate-string body))))]
      (is (= 201 (:status response)))))

  (testing "Response has JSON content type"
    (let [response (core/handler (mock/request :get "/api/users"))]
      (is (or (contains? (:headers response) "Content-Type")
              (contains? (:headers response) "content-type"))))))
