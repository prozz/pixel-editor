(ns pixel-editor.utils
  "bunch of utility functions used across whole project")

(defn symbol->str
  "replaces symbol with string or leave input untouched"
  [x]
  (if (symbol? x) (str x) x))

(defn decrement
  "replaces int with decremented int or leave input untouched"
  [x]
  (if (int? x) (dec x) x))

(defn bye
  []
  (println "\nBye!")
  (System/exit 0))
