(ns pixel-editor.validators
  "validators for user input from cli"
  (:require [pixel-editor.core :refer [width height]]))

(def max-size 250)

(defn- within-max-size?
  [x]
  (and (nat-int? x)
       (<= 1 x max-size)))

(defn within-width?
  "checks if x is between 1 and image width"
  [image x]
  (and (nat-int? x)
       (<= 1 x (width image))))

(defn within-height?
  "checks if y is between 1 and image height"
  [image y]
  (and (nat-int? y)
       (<= 1 y (height image))))

(defn within-image?
  "checks if pixel belongs to image"
  [image [x y]]
  (and (within-width? image x)
       (within-height? image y)))

(defn- is-colour?
  [x]
  (and (string? x) (= 1 (count x))))

(defn new-image?
  "validates args for new-image command (I)"
  [args]
  (and (= 2 (count args))
       (let [[x y] args]
         (and (within-max-size? x)
              (within-max-size? y)))))

(defn concentric-square?
  "validates args for concentric-square command (K)"
  [image args]
  (and (< 2 (count args))
       (within-image? image [(first args) (second args)])
       (every? is-colour? (nthnext args 2))))

(defn pixel-with-colour?
  "validates args for colour (L) and fill-region (F) commands"
  [image args]
  (and (= 3 (count args))
       (let [[x y c] args]
         (and (within-width? image x)
              (within-height? image y)
              (is-colour? c)))))

(defn vertical-line?
  "validates args for vertical-line command (V)"
  [image args]
  (and (= 4 (count args))
       (let [[x y1 y2 c] args]
         (and (within-width? image x)
              (within-height? image y1)
              (within-height? image y2)
              (< y1 y2)
              (is-colour? c)))))

(defn horizontal-line?
  "validates args for horizontal-line command (H)"
  [image args]
  (and (= 4 (count args))
       (let [[x1 x2 y c] args]
         (and (within-width? image x1)
              (within-width? image x2)
              (< x1 x2)
              (within-height? image y)
              (is-colour? c)))))
