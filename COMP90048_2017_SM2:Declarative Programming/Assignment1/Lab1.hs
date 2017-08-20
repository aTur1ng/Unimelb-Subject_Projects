-- @author aTur1ng

module Lab1 (subst, interleave, unroll) where

subst :: Eq t => t -> t -> [t] -> [t]
subst a b [] = []
subst a b (x:xs) 
  | x == a = (b:subst a b xs)
  | otherwise = (x:subst a b xs)
    
interleave :: [t] -> [t] -> [t]
interleave [] [] = []
interleave l1 [] = l1
interleave [] l2 = l2
interleave (x:xs) (y:ys) = (x:y:interleave xs ys)

unroll :: Int -> [a] -> [a]
unroll 0 _ = []
unroll _ [] = []
unroll n l 
	 | n > length l = take n (cycle l)
	 | otherwise = take n l
