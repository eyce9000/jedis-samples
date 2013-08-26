package grl.redis.users;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;

public class HashSample {
	public static void main(String[] args){
		Jedis redis = new Jedis("localhost");
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("email","user1@domain.com");
		map.put("userid","12356");
		map.put("address","1111 Main St. Houston TX, 77054");
		map.put("phone","555-124-5544");
		
		//Store all values in the map
		//REDIS HMSET
		redis.hmset("user.by.id."+map.get("userid"), map);
		
		//Index this user by their email
		redis.set("user.id.by.email.", map.get("email"));
		
		System.out.println("User stored: "+map.get("userid"));
		
		//Confirm that all values are set
		//REDIS HGETALL
		System.out.println(
				redis.hgetAll("user.by.id."+map.get("userid"))
		);
		
		map = new HashMap<String,String>();
		map.put("email","user2@other.domain.com");
		map.put("userid","24567");
		map.put("address","1111 Main St. Houston TX, 77054");
		map.put("phone","555-124-5544");
		
		//Store all values in the map
		//REDIS HMSET
		redis.hmset("user.by.id."+map.get("userid"), map);
		
		//Index this user by their email
		redis.set("user.id.by.email.", map.get("email"));
		
		System.out.println("User stored: "+map.get("userid"));

		//Confirm that all values are set
		//REDIS HGETALL
		System.out.println(
				redis.hgetAll("user.by.id."+map.get("userid"))
		);
		
		
		//Get user by email using String key
		String email = "user1@domain.com";
		String userid = redis.get("user.id.by.email."+email);
		if(userid!=null){
			System.out.println(String.format(
					"Found user by email: %s",
					redis.hgetAll("user.by.id."+userid)
			));
		}
	}
}
