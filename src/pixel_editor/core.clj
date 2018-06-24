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

(defn- draw-point
  "draws point on the image with given colour"
  [image [x y] colour]
  (assoc-in image [y x] colour))

(defn- draw-points
  "draws series of points on the image with given colour"
  [image points colour]
  (reduce #(draw-point %1 %2 colour) image points))

(defn colour
  "api command. colours point of the image with given colour"
  [image [x y colour]]
  (draw-point image [x y] colour))

(defn clear
  "api command. clear whole image and sets default colour to each pixel"
  [image]
  ; it's easier to just recreate the image from scratch
  (create-image (width image) (height image)))

(defn vertical-points
  "generates all vertical points for given coords"
  [x y1 y2]
  (for [y (range y1 (inc y2))] [x y]))

(defn vertical-line
  "api command. draws vertical line for given coords and colour"
  [image [x y1 y2 colour]]
  (draw-points image (vertical-points x y1 y2) colour))

(defn horizontal-points
  "generates all horizontal points for given coords"
  [x1 x2 y]
  (for [x (range x1 (inc x2))] [x y]))

(defn horizontal-line
  "api command. draws horizontal line for given coords and colour"
  [image [x1 x2 y colour]]
  (draw-points image (horizontal-points x1 x2 y) colour))

(defn image->str
  "api command. turns internal image representation into user friendly string"
  [image]
  (str/join "\n" (map #(str/join %1) image)))

(defn- add-delta
  "creates new point from old"
  [x y delta]
  (map + [x y] delta))

(defn within-image?
  "checks if given point belongs to image"
  [image [x y]]
  (and (< -1 x (width image))
       (< -1 y (height image))))

(defn adjacent-points
  "adjacent points for given point, only those sharing side, corners are excluded"
  [image [x y]]
  (->> [[-1 0] [0 -1] [0 1] [1 0]]
       (map (partial add-delta x y))
       (filter (partial within-image? image))))

(defn colour-of
  "returns colour of given image point"
  [image [x y]]
  (get-in image [y x]))

(defn points-of-colour
  "filters out points not matching given colour"
  [image points colour]
  (filter #(= colour (colour-of image %1)) points))

(defn adjacent-points-of-colour
  "adjacent points matching same colour"
  [image point]
  (points-of-colour image (adjacent-points image point) (colour-of image point)))

(defn adjacent-points-of-colour*
  "adjacent points matching same colour, unless excluded"
  [image point excluded]
  (remove #(contains? excluded %1) (adjacent-points-of-colour image point)))

(defn region-points
  "recursively builds vector of adjacent coords matching colour of the starting point"
  [image x y]
  (loop [visited #{} to-visit #{[x y]}]
    (if (empty? to-visit)
      (vec visited)
      (let [point (first to-visit)]
        (recur (conj visited (vec point))
               (set (concat (rest to-visit)
                            (adjacent-points-of-colour* image point visited))))))))

(defn fill-region
  "api command. fills region (defined as set of adjacent points sharing a colour) with new colour"
  [image [x y colour]]
  (draw-points image (region-points image x y) colour))
