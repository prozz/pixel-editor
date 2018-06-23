(ns pixel-editor.utils)

(defn symbol->str
  "replaces symbol with string or leave input untouched"
  [x]
  (if (symbol? x) (str x) x))

(defn decrement
  "replaces number with decremented number or leave input untouched"
  [x]
  (if (int? x) (dec x) x))

(defn bye
  []
  (println "\nBye!")
  (System/exit 0))
