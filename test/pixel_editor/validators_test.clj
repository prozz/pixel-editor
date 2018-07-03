(ns pixel-editor.validators-test
  (:require [clojure.test :refer :all]
            [pixel-editor.validators :refer :all]
            [pixel-editor.core :refer [create-image]]))

(deftest validators

  (let [image (create-image 2 5)]
    (testing "within-width?"
      (let [valid [1 2]
            invalid [-1 0 3 \a "a" 1.1]]
        (is (every? true? (map (partial within-width? image) valid)))
        (is (every? false? (map (partial within-width? image) invalid)))))

    (testing "within-height?"
      (let [valid [1 2 3 4 5]
            invalid [-1 0 6 \a "a" 1.1]]
        (is (every? true? (map (partial within-height? image) valid)))
        (is (every? false? (map (partial within-height? image) invalid)))))

    (testing "new-image?"
      (let [valid [[1 250] [2 249] [250 1]]
            invalid [[0 250] [1 251] [250 0] [\a \b] [1.1 249.1] [1 250 1]]]
        (is (every? true? (map new-image? valid)))
        (is (every? false? (map new-image? invalid)))))

    (testing "pixel-with-colour?"
      (let [valid [[1 5 "F"] [2 1 "G"]]
            invalid [[1 5 \F] [1 5 "FF"] [0 5 "F"] [1 6 "F"] [3 5 "G"] [2 0 "G"]]]
        (is (every? true? (map (partial pixel-with-colour? image) valid)))
        (is (every? false? (map (partial pixel-with-colour? image) invalid)))))

    (testing "vertical-line?"
      (let [valid [[1 1 5 "F"] [2 1 5 "F"]]
            invalid [[0 1 5 "F"] [3 1 5 "F"] [1 0 6 "F"] [1 5 1 "F"] [1 1 5 \F] [1 1 5 "FF"] [1 1 5]]]
        (is (every? true? (map (partial vertical-line? image) valid)))
        (is (every? false? (map (partial vertical-line? image) invalid)))))

    (testing "horizontal-line?"
      (let [valid [[1 2 1 "F"] [1 2 5 "F"]]
            invalid [[1 2 0 "F"] [1 2 6 "F"] [0 3 1 "F"] [2 1 1 "F"] [1 2 1 \F] [1 2 1 "FF"] [1 2 1]]]
        (is (every? true? (map (partial horizontal-line? image) valid)))
        (is (every? false? (map (partial horizontal-line? image) invalid)))))

    (testing "concentric-square?"
      (let [valid [[2 2 "A"] [2 2 "T" "Y"]]
            invalid [[2 2] [1] [1 1 \T]]]
        (is (every? true? (map (partial concentric-square? image) valid)))
        (is (every? false? (map (partial concentric-square? image) invalid)))))))

