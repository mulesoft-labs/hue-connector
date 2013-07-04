# Philips Hue Connector

<img src="http://cdn.macrumors.com/article-new/2013/05/philips_hue_starter_pack_iphone.jpg" />

Allows Mule to interact with [Philips Hue Lightbulbs](http://meethue.com). Hue light bulbs are
wifi connected and have wide color range allowing to change the colour hue and brightness of a bulb or
group of bulbs


## Testing

To run the integration tests you need to be connected to a nework with a PHilip's Hue bridge connected. Yuo also need to set
two properties in a file called 'mule-test.properties' in the root of your home directory. These properties are:
 hue.ipAddress - the ip address fo the bridge
 hue.username - your hue account username

 Then you can run the integration tests using the 'it' profile, i.e.

    mvn -Pit clean verify