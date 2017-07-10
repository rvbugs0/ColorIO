# ColorIO
<hr>
<br/>

A web tool built for lazy programmers, that uses the combinaⷺon of Breadth First Search <br/>
algorithm and Regular expressions to extract out colors used in a website by looking up css<br/>
files for color codes and related syntaxes specified using regular expressions.

```

ColorIOUtils ciu = new ColorIOUtils();
java.util.Hashtable<String,Collection> urlToColorSetMapping =  ciu.findColors(URL);

```

Contains a java Collection (HashSet) for each url visited.

The Url's can be extracted using 

```

java.util.Enumeration urls=urlToColorSetMapping.keys();


```
