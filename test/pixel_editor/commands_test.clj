(ns pixel-editor.commands-test
  (:require [clojure.test :refer :all]
            [pixel-editor.commands :as cmd]
            [pixel-editor.core :refer [image->str]]))

(deftest integration
  (testing "Q extension"
    ; > I 6 6
    ; > K 4 4 A B C
    (cmd/run-command "I" [6 6])
    (cmd/run-command "K" [4 4 "A" "B" "C"])
    (is (= (str "OOOOOO\n"
                "OCCCCC\n"
                "OCBBBC\n"
                "OCBABC\n"
                "OCBBBC\n"
                "OCCCCC") (image->str @cmd/current-image))))
  (testing "Q extension outside image"
    ; > I 6 6
    ; > K 5 5 A B C
    (cmd/run-command "I" [6 6])
    (cmd/run-command "K" [5 5 "A" "B" "C"])
    (is (= (str "OOOOOO\n"
                "OOOOOO\n"
                "OOCCCC\n"
                "OOCBBB\n"
                "OOCBAB\n"
                "OOCBBB") (image->str @cmd/current-image))))
  (testing "real scenario #1"
    ; > I 4 7 
    ; > L 2 3 T
    ; > L 1 4 G
    ; > F 1 1 Y
    (cmd/run-command "I" [4 7])
    (cmd/run-command "L" [2 3 "T"])
    (cmd/run-command "L" [1 4 "G"])
    (cmd/run-command "F" [1 1 "Y"])
    (is (= (str "YYYY\n"
                "YYYY\n"
                "YTYY\n"
                "GYYY\n"
                "YYYY\n"
                "YYYY\n"
                "YYYY") (image->str @cmd/current-image))))
  (testing "from docs"
    ; > I 5 6
    ; > L 2 3 A
    (cmd/run-command "I" [5 6])
    (cmd/run-command "L" [2 3 "A"])
    (is (= (str "OOOOO\n"
                "OOOOO\n"
                "OAOOO\n"
                "OOOOO\n"
                "OOOOO\n"
                "OOOOO") (image->str @cmd/current-image)))
    ; > F 3 3 J
    ; > V 2 3 4 W
    ; > H 3 4 2 Z
    (cmd/run-command "F" [3 3 "J"])
    (cmd/run-command "V" [2 3 4 "W"])
    (cmd/run-command "H" [3 4 2 "Z"])
    (is (= (str "JJJJJ\n"
                "JJZZJ\n"
                "JWJJJ\n"
                "JWJJJ\n"
                "JJJJJ\n"
                "JJJJJ") (image->str @cmd/current-image)))))
