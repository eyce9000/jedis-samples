package grl.redis.users;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class KeysSample {
	public static void main(String[] args){
		HashMap<String,String> users = new HashMap<String,String>();
		
		users.put("12356","user1@domain.com");
		users.put("24567","user2@other.domain.com");
		users.put("56123","user3@domain.com");
		
		Jedis redis = new Jedis("localhost");
		
		for(Entry<String,String> entry:users.entrySet()){
			String userid = entry.getKey();
			String email = entry.getValue();
			
			//Cache user userid by email
			//REDIS SET
			redis.set("user.id.by.email."+email,userid);
			
			//Set expiration in seconds
			//REDIS EXPIRE
			redis.expire("user.id.by.email."+email, 60*60);
			
			System.out.println(String.format(
					"User added to id-by-email index: %s -> %s",
					email,
					redis.get("user.id.by.email."+email)		
			));
			System.out.println(String.format(
					"Time before expiration: %d seconds\n",
					redis.ttl("user.id.by.email."+email)
			));
		}
		
		//Search for userids associated with a domain
		//REDIS KEYS
		Set<String> keys = redis.keys("user.id.by.email.*@domain.com");
		
		System.out.println("\nUser Ids matching '*@domain.com':");
		for(String key:keys){
			System.out.println("\t"+redis.get(key));
		}
	}
}
