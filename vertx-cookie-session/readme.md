# vertx-cookie-session

Implements a Vertx.io session handler which stores all session data in an encrypted cookie. The advantage is easy scaling over multiple servers, even without running Vertx is cluster mode. However the amount of data that can be stored is quite small. The cookie can store up to 4k, but the java serialization and encryption consume extra bytes.


## Installation

Just add this project to your workspace and add the following maven dependency:


<dependency>
	<groupId>vommond.de</groupId>
	<artifactId>vertx-cookie-session</artifactId>
	<version>1.0</version>
</dependency>
		

## Usage

```java

// Init router
Router router = Router.router(vertx);
...

// add CookieSessionHandler
router.route().handler(new CookieSesssionHandler("MyPassword"));

// example rest service that increments counter for a session
router.route(HttpMethod.GET, "/count").handler(context ->{
				
	Session s = context.session();
	Integer count = s.get("count");
	if(count == null){
		count = new Integer(1);
	} else {
		count = new Integer(count.intValue() + 1);
	}
	s.put("count", count);
	context.response().end("{\"count\": " +  count +"}");
});
		
```

If you want to share the sessions in a cluster, make sure you use the same password on all machines.


## License

Apache License 2.0