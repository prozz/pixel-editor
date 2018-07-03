(ns pixel-editor.core
  "pixel editor core logic"
  (:require [clojure.string :as str]))

(def white \O)

(defn create-image
  "api command. creates image of given size with default colour
     m - width
     n - height
   represented internally as vector of vectors (rows)"
  [m n]
  (vec (repeatedly n #(vec (take m (repeat white))))))

(defn width
  "image width, from 1 to max-size"
  [image]
  (count (first image)))

(defn height
  "image height, from 1 to max-size"
  [image]
  (count image))

(defn- draw-pixel
  "draws pixel on the image with given colour"
  [image [x y] colour]
  (assoc-in image [y x] colour))

(defn- draw-pixels
  "draws series of pixels on the image with given colour"
  [image pixels colour]
  (reduce #(draw-pixel %1 %2 colour) image pixels))

(defn colour
  "api command. colours pixel of the image with given colour"
  [image [x y colour]]
  (draw-pixel image [x y] colour))

(defn clear
  "api command. clear whole image and sets default colour to each pixel"
  [image]
  ; it's easier to just recreate the image from scratch
  (create-image (width image) (height image)))

(defn vertical-pixels
  "generates all vertical pixels for given coords"
  [x y1 y2]
  (for [y (range y1 (inc y2))] [x y]))

(defn vertical-line
  "api command. draws vertical line for given coords and colour"
  [image [x y1 y2 colour]]
  (draw-pixels image (vertical-pixels x y1 y2) colour))

(defn horizontal-pixels
  "generates all horizontal pixels for given coords"
  [x1 x2 y]
  (for [x (range x1 (inc x2))] [x y]))

(defn horizontal-line
  "api command. draws horizontal line for given coords and colour"
  [image [x1 x2 y colour]]
  (draw-pixels image (horizontal-pixels x1 x2 y) colour))

(defn image->str
  "api command. turns internal image representation into user friendly string"
  [image]
  (str/join "\n" (map #(str/join %1) image)))

(defn- add-delta
  "creates new pixel from old"
  [x y delta]
  (map + [x y] delta))

(defn within-image?
  "checks if given pixel belongs to image"
  [image [x y]]
  (and (< -1 x (width image))
       (< -1 y (height image))))

(def ^:private top-right [1 -1])
(def ^:private top-left [-1 -1])
(def ^:private bottom-right [1 1])
(def ^:private bottom-left [-1 1])

(def ^:private left [-1 0])
(def ^:private right [1 0])
(def ^:private top [0 1])
(def ^:private bottom [0 -1])

(defn adjacent-pixels
  "adjacent pixels for given pixel, only those sharing side, corners are excluded"
  [image [x y]]
  (->> [left bottom top right]
       (map (partial add-delta x y))
       (filter (partial within-image? image))))

(defn colour-of
  "returns colour of given image pixel"
  [image [x y]]
  (get-in image [y x]))

(defn pixels-of-colour
  "filters out pixels not matching given colour"
  [image pixels colour]
  (filter #(= colour (colour-of image %1)) pixels))

(defn adjacent-pixels-of-colour
  "adjacent pixels matching same colour"
  [image pixel]
  (pixels-of-colour image (adjacent-pixels image pixel) (colour-of image pixel)))

(defn adjacent-pixels-of-colour*
  "adjacent pixels matching same colour, unless excluded"
  [image pixel excluded]
  (remove #(contains? excluded %1) (adjacent-pixels-of-colour image pixel)))

(defn region-pixels
  "recursively builds vector of adjacent coords matching colour of the starting pixel"
  [image x y]
  (loop [visited #{} to-visit #{[x y]}]
    (if (empty? to-visit)
      (vec visited)
      (let [pixel (first to-visit)]
        (recur (conj visited (vec pixel))
               (set (concat (rest to-visit)
                            (adjacent-pixels-of-colour* image pixel visited))))))))

(defn fill-region
  "api command. fills region (defined as set of adjacent pixels sharing a colour) with new colour"
  [image [x y colour]]
  (draw-pixels image (region-pixels image x y) colour))

(defn- scale
  [[x y] depth]
  (mapv (partial * depth) [x y]))

(defn- corner
  [delta [x y] depth]
  (add-delta x y (scale delta depth)))

(defn square-pixels
  "pixels forming a square shape for given coords with radius"
  [image [x y] radius]
  (let [[x1 y1] (corner top-left [x y] radius)
        [x2 y2] (corner top-right [x y] radius)
        [x3 y3] (corner bottom-left [x y] radius)
        [x4 y4] (corner bottom-right [x y] radius)
        top-edge (horizontal-pixels x1 x2 y1)
        bottom-edge (horizontal-pixels x3 x4 y3)
        left-edge (vertical-pixels x1 y1 y3)
        right-edge (vertical-pixels x2 y2 y4)]
    (filter (partial within-image? image)
            (concat top-edge bottom-edge left-edge right-edge))))

(defn concentric-square
  "api command. draws concentric squares around starting pixel, one for every colour"
  [image [x y & colours]]
  (reduce (fn [image [pixels colour]] (draw-pixels image pixels colour))
          image
          (map-indexed #(vector (square-pixels image [x y] %1) %2) colours)))
