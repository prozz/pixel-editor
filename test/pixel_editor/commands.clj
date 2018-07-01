(ns pixel-editor.commands
  (:require [clojure.test :refer :all]
            [pixel-editor.commands :as cmd]
            [pixel-editor.core :refer [image->str]]))

(deftest integration
 ; (init!)

  (testing "real scenario #1"
    ; > I 4 7 
    ; > L 2 3 T
    ; > L 1 4 G
    ; > F 1 1 Y
    (println (deref cmd/current-image))
    (println @cmd/commands)
    (cmd/run-command "I" [4 7])
    (cmd/run-command "L" [2 3 \T])
    (cmd/run-command "L" [1 4 \G])
    (cmd/run-command "F" [1 1 \Y])
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
    (cmd/run-command "L" [2 3 \A])
    (is (= (str "OOOOO\n"
                "OOOOO\n"
                "OAOOO\n"
                "OOOOO\n"
                "OOOOO\n"
                "OOOOO") (image->str @cmd/current-image)))

    ; > F 3 3 J
    ; > V 2 3 4 W
    ; > H 3 4 2 Z
    (cmd/run-command "F" [3 3 \J])
    (cmd/run-command "V" [2 3 4 \W])
    (cmd/run-command "H" [3 4 2 \Z])
    (is (= (str "JJJJJ\n"
                "JJZZJ\n"
                "JWJJJ\n"
                "JWJJJ\n"
                "JJJJJ\n"
                "JJJJJ") (image->str @cmd/current-image)))))
