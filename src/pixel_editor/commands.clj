(ns pixel-editor.commands
  "commands implementation used by command line interface"
  (:require [clojure.string :as str]
            [pixel-editor.core :as c]
            [pixel-editor.utils :as u]
            [pixel-editor.validators :as v]))

(def current-image (atom nil))

(defn- apply-on-image
  [f]
  (if (nil? @current-image)
    (throw (ex-info "No image." {}))
    (f)))

(defn normalize
  "adjusting to indexing from 0, needed by all commands but new-image"
  [args]
  (map u/decrement args))

(defn new-image
  [& args]
  {:pre [(v/new-image? args)]}
  (reset! current-image (apply c/create-image args)))

(defn show
  [& args]
  {:pre [(empty? args)]}
  (apply-on-image #(println (str "=>\n" (c/image->str @current-image)))))

(defn clear
  [& args]
  {:pre [(empty? args)]}
  (apply-on-image #(swap! current-image c/clear)))

(defn colour
  [& args]
  {:pre [(v/pixel-with-colour? @current-image args)]}
  (apply-on-image #(swap! current-image c/colour (normalize args))))

(defn vertical-line
  [& args]
  {:pre [(v/vertical-line? @current-image args)]}
  (apply-on-image #(swap! current-image c/vertical-line (normalize args))))

(defn horizontal-line
  [& args]
  {:pre [(v/horizontal-line? @current-image args)]}
  (apply-on-image #(swap! current-image c/horizontal-line (normalize args))))

(defn fill-region
  [& args]
  {:pre [(v/pixel-with-colour? @current-image args)]}
  (apply-on-image #(swap! current-image c/fill-region (normalize args))))

(defn quit
  [& args]
  {:pre [(empty? args)]}
  (u/bye))

(defn print-help
  [syntax]
  (println (str/join "\n" (vals syntax))))
