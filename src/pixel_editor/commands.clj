(ns pixel-editor.commands
  "commands definitions used by command line interface"
  (:require [clojure.string :as str]
            [pixel-editor.commands-core :refer :all]
            [pixel-editor.core :as c]
            [pixel-editor.utils :as u]
            [pixel-editor.validators :as v]))

(defn print-help
  []
  (println (str/join "\n" (get-descriptions))))

(defn init!
  "initialize all commands"
  []
  (command! "I"
    "I <width> <height> - creates new image with given dimensions (from 1 to 250)"
    (args-validator v/new-image?)
    (reset-image! c/create-image))

  (command! "S"
    "S - show the content of the current image"
    (args-validator empty?)
    (with-image (fn [image args] (println (str "=>\n" (c/image->str image))))))

  (command! "C"
    "C - clears current image"
    (args-validator empty?)
    (update-image! (fn [image args] (c/clear image))))

  (command! "L"
    "L <x> <y> <colour> - colours a pixel located at (<x>, <y>) with <colour>"
    (image-args-validator v/pixel-with-colour?)
    (update-image! c/colour))

  (command! "V"
    "V <x> <y1> <y2> <colour> - draws vertical line of given <colour> in column <x> from row <y1> to <y2>"
    (image-args-validator v/vertical-line?)
    (update-image! c/vertical-line))

  (command! "H"
    "H <x1> <x2> <y> <colour> - draws horizontal line of given <colour> in row <y> from column <x1> to <x2>"
    (image-args-validator v/horizontal-line?)
    (update-image! c/horizontal-line))

  (command! "F"
    "F <x> <y> <colour> - fills nearby pixels with same colour, starting with pixel located at (<x>, <y>) with <colour>"
    (image-args-validator v/pixel-with-colour?)
    (update-image! c/fill-region))

  (command! "Q"
    "Q, ctrl-d - quit"
    (args-validator empty?)
    (fire u/bye))

  (command! "help"
    "help - prints help message"
    (args-validator empty?)
    (fire print-help)))
