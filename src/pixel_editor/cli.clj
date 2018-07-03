(ns pixel-editor.cli
  "command line interface for pixel editor"
  (:require [pixel-editor.commands :as cmd]
            [clojure.repl :as r]
            [pixel-editor.utils :as u])
  (:gen-class))

(defn handle
  "parses user input and applies suitable command"
  [line]
  (let [input (map u/symbol->str (read-string (str "[" line "]")))
        cmd (first input)
        args (rest input)]
    (try
      (cmd/run-command cmd args)
      (catch clojure.lang.ExceptionInfo e (println (.getMessage e) e)))))

(defn prompt-read-line
  "prints prompt and wait for user input"
  []
  (print "> ")
  (flush)
  (read-line))

(defn -main
  [& args]

  (r/set-break-handler! (fn [s] (u/bye)))

  (println "=>\nWelcome to pixel-editor!")
  (cmd/print-help)

  (doseq [line (repeatedly prompt-read-line) :while line]
    (try
      (handle line)
      (catch Exception e (println "Unknown command. Type 'help' for help."))))

  (u/bye))

