terraingen
==========

terraingen
==========

(Forced push with "git push --force -u origin master" on 8/19/2014. 25 commits are missing. Odd.)

This project is for experimenting with terrain in Java. It is rendered with Processing for convenience. I recommend creating a project from source in Eclipse.

There are two main classes: Main and DiamondSquareTest. In the Main class, simple Perlin noise is made and rendered as a set of islands. For now, the terrain has a constant "seed". There is a sea level and anything below it is rendered as ocean while anyhting above it is land. Use "u" and "j" to raise and lower the sea level, respectively. In the DiamondSquareTest class, terrain made using a iterative (not recursive) diamond-square algorithm based on this article on the subject (http://www.gameprogrammer.com/fractal.html). Use "i" and "o" to zoom in and out. 
