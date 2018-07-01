(ns pixel-editor.commands-core
  "command line interface internal api"
  (:require [pixel-editor.utils :as u]))

(def current-image (atom nil))

(def commands (atom []))

(defn command!
  "registers command with given params"
  [name desc validator command]
  (swap! commands conj {:name name
                        :desc desc
                        :validator validator
                        :command command}))

(defn get-command
  "gets command with given name"
  [name]
  (first (filter #(= (:name %1) name) @commands)))

(defn get-descriptions
  "gets all commands descriptions, useful for building help info"
  []
  (map :desc @commands))

(defn normalize
  "adjusting to indexing from 0, needed by all commands but new-image"
  [args]
  (map u/decrement args))

(defn apply-on-image
  "fires function only when image exists"
  [f]
  (if (nil? @current-image)
    (throw (ex-info "No image." {}))
    (f)))

(defn update-image!
  "wrapper for image updating function, changes image state, command args are normalized by default"
  ([f]
   (update-image! f normalize))
  ([f af]
   (fn [image args] (apply-on-image #(swap! image f (af args))))))

(defn reset-image!
  "wrapper for image resetting function, changes image state"
  [f]
  (fn [image args] (reset! image (apply f args))))

(defn with-image
  "wrapper for image using function, doesn't change image state"
  [f]
  (fn [image args] (apply-on-image #(f @image args))))

(defn fire
  "wrapper for any, non image related, function"
  [f]
  (fn [image args] (f)))

(defn image-args-validator
  "wrapper for validator function, with image and args as params"
  [f]
  (fn [image args] (f image args)))

(defn args-validator
  "wrapper for validator function, with just args as param"
  [f]
  (fn [image args] (f args)))

(defn run-command
  "validates and runs command, throws ex-info on failed validation"
  [name args]
  (let [cmd (get-command name)]
    (if ((:validator cmd) @current-image args)
      ((:command cmd) current-image args)
      (throw (ex-info (str "Syntax error: " (:desc cmd)) {})))))
