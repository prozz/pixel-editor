(ns pixel-editor.core-test
  (:require [clojure.test :refer :all]
            [pixel-editor.core :refer :all]
            [pixel-editor.commands :refer [normalize]]))

(deftest basics
  (let [image (create-image 2 3)]
    (testing "create images"
      (is (vector? image))
      (is (= 3 (count image)))
      (is (= 2 (count (first image)))))
    (testing "image size"
      (is (= 2 (width image)))
      (is (= 3 (height image))))
    (testing "image to string"
      (is (= "OO\nOO\nOO" (image->str image))))))

(deftest drawing
  (let [image (create-image 2 2)]
    (testing "colour"
      (is (= [[\B \O] [\O \O]] (colour image [0 0 \B])))
      (is (= [[\O \O] [\O \B]] (colour image [1 1 \B])))
      (is (= [[\O \O] [\B \O]] (colour image [0 1 \B])))
      (is (= [[\O \B] [\O \O]] (colour image [1 0 \B]))))
    (testing "vertical line"
      (is (= [[\B \O] [\O \O]] (vertical-line image [0 0 0 \B])))
      (is (= [[\B \O] [\B \O]] (vertical-line image [0 0 1 \B])))
      (is (= [[\O \B] [\O \O]] (vertical-line image [1 0 0 \B])))
      (is (= [[\O \B] [\O \B]] (vertical-line image [1 0 1 \B]))))
    (testing "horizontal line"
      (is (= [[\B \O] [\O \O]] (horizontal-line image [0 0 0 \B])))
      (is (= [[\O \O] [\B \O]] (horizontal-line image [0 0 1 \B])))
      (is (= [[\B \B] [\O \O]] (horizontal-line image [0 1 0 \B])))
      (is (= [[\O \O] [\B \B]] (horizontal-line image [0 1 1 \B]))))))

(deftest filling
  (testing "within-image?"
    (is (true? (within-image? (create-image 2 3) [0 0])))
    (is (false? (within-image? (create-image 2 3) [2 0]))))
  (testing "adjacent-points"
    (is (= '((1 3) (2 2) (2 4) (3 3)) (adjacent-points (create-image 5 5) [2 3])))
    (is (= '((0 1) (1 0)) (adjacent-points (create-image 2 2) [0 0])))
    (is (= '((0 1) (1 0)) (adjacent-points (create-image 2 2) [1 1])))
    (is (empty? (adjacent-points (create-image 2 2) [2 2])))
    (is (= '((0 2) (1 1)) (adjacent-points (create-image 2 3) [1 2]))))
  (let [image (-> (create-image 3 3)
                  (colour [0 0 \T])
                  (colour [1 1 \G])
                  (colour [2 2 \B]))
        image3x5 (-> (create-image 3 5)
                     (colour [1 1 \G]))
        points (for [x (range 3) y (range 3)] [x y])]
    (testing "colour-of"
      (is (= \G (colour-of image [1 1])))
      (is (= \O (colour-of image [0 1]))))
    (testing "points-of-colour"
      (is (= 1 (count (points-of-colour image points \T))))
      (is (= 1 (count (points-of-colour image points \G))))
      (is (= 1 (count (points-of-colour image points \B))))
      (is (= 6 (count (points-of-colour image points \O)))))
    (testing "region-points"
      (is (= 1 (count (region-points image 0 0))))
      (is (= 1 (count (region-points image 1 1))))
      (is (= 1 (count (region-points image 2 2))))
      (is (= 3 (count (region-points image 0 1))))
      (is (= 3 (count (region-points image 1 0))))
      (is (= (region-points image 0 1) (region-points image 0 2)))
      (is (not= (region-points image 0 1) (region-points image 1 0)))
      (is (= 14 (count (region-points image3x5 0 0)))))))

(deftest integration

  (testing "real scenario #1"
    ;; > I 4 7 
    ;; > L 2 3 T
    ;; > L 1 4 G
    ;; > F 1 1 Y
    (let [image (-> (create-image 4 7)
                    (colour (normalize [2 3 \T]))
                    (colour (normalize [1 4 \G]))
                    (fill-region (normalize [1 1 \Y])))
          image-str (image->str image)]
      (println image-str)
      (is (= (str "YYYY\n"
                  "YYYY\n"
                  "YTYY\n"
                  "GYYY\n"
                  "YYYY\n"
                  "YYYY\n"
                  "YYYY") image-str)))))
