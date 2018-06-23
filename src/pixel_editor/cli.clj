(ns pixel-editor.cli
  (:require [clojure.repl :as r]
            [pixel-editor.commands :as cmd]
            [pixel-editor.utils :as u]
            [pixel-editor.core :as c])
  (:gen-class))

(def syntax {"I" "I <width> <height> - creates new image with given dimensions (from 1 to 250)"
             "S" "S - show the content of the current image"
             "C" "C - clears current image"
             "L" "L <x> <y> <colour> - colours a pixel located at (<x>, <y>) with <colour>"
             "V" "V <x> <y1> <y2> <colour> - draws vertical line of given <colour> in column <x> from row <y1> to <y2>"
             "H" "H <x1> <x2> <y> <colour> - draws horizontal line of given <colour> in row <y> from column <x1> to <x2>"
             "F" "F <x> <y> <colour> - fills nearby pixels with same colour, starting with pixel located at (<x>, <y>) with <colour>"
             "Q" "Q, ctrl-d - quit"})

(def commands {"I" cmd/new-image
               "S" cmd/show
               "C" cmd/clear
               "L" cmd/colour
               "V" cmd/vertical-line
               "H" cmd/horizontal-line
               "F" cmd/fill-region
               "Q" cmd/quit
               "help" #(cmd/print-help syntax)})

(defn handle [line]
  (let [input (map u/symbol->str (read-string (str "[" line "]")))
        cmd (first input)
        args (rest input)]
    (try
      (apply (get commands cmd) args)
      (catch AssertionError e (println "Syntax error:" (get syntax cmd))))))

(defn prompt-read-line
  []
  (print "> ")
  (flush)
  (read-line))

(defn -main
  [& args]

  (r/set-break-handler! (fn [s] (u/bye)))

  (println "=>\nWelcome to pixel-editor!")
  (cmd/print-help syntax)

  (doseq [line (repeatedly prompt-read-line) :while line]
    (try
      (handle line)
      (catch Exception e (println "Unknown command. Type 'help' for help." e))))

  (u/bye))

