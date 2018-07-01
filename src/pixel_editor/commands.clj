(ns pixel-editor.commands
  "commands implementation used by command line interface"
  (:require [clojure.string :as str]
            [pixel-editor.core :as c]
            [pixel-editor.utils :as u]
            [pixel-editor.validators :as v]))

(def current-image (atom nil))

(def commands (atom []))

(defn defcommand
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

(defn update-image
  "wrapper for image updating function, changes image state, command args are normalized by default"
  ([f]
   (update-image f normalize))
  ([f af]
   (fn [image args] (apply-on-image #(swap! image f (af args))))))

(defn reset-image
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


(defn print-help
  []
  (println (str/join "\n" (get-descriptions))))

(defn init!
  []
  (defcommand "I"
    "I <width> <height> - creates new image with given dimensions (from 1 to 250)"
    (args-validator v/new-image?)
    (reset-image c/create-image))

  (defcommand "S"
    "S - show the content of the current image"
    (args-validator empty?)
    (with-image (fn [image args] (println (str "=>\n" (c/image->str image))))))

  (defcommand "C"
    "C - clears current image"
    (args-validator empty?)
    (update-image c/clear))

  (defcommand "L"
    "L <x> <y> <colour> - colours a pixel located at (<x>, <y>) with <colour>"
    (image-args-validator v/pixel-with-colour?)
    (update-image c/colour))

  (defcommand "V"
    "V <x> <y1> <y2> <colour> - draws vertical line of given <colour> in column <x> from row <y1> to <y2>"
    (image-args-validator v/vertical-line?)
    (update-image c/vertical-line))

  (defcommand "H"
    "H <x1> <x2> <y> <colour> - draws horizontal line of given <colour> in row <y> from column <x1> to <x2>"
    (image-args-validator v/horizontal-line?)
    (update-image c/horizontal-line))

  (defcommand "F"
    "F <x> <y> <colour> - fills nearby pixels with same colour, starting with pixel located at (<x>, <y>) with <colour>"
    (image-args-validator v/pixel-with-colour?)
    (update-image c/fill-region))

  (defcommand "Q"
    "Q, ctrl-d - quit"
    (args-validator empty?)
    (fire u/bye))

  (defcommand "help"
    "help - prints help message"
    (args-validator empty?)
    (fire print-help)))
