package grl.redis.users;

import java.util.Set;

import redis.clients.jedis.Jedis;

public class SetSample {
	public static void main(String[] args){
		Jedis redis = new Jedis("localhost");
		
		Set<String> keys = redis.keys("user.by.id.*");
		for(String key:keys){
			//Get field from hash
			//REDIS HGET
			String userid = redis.hget(key, "userid");
			
			//Add user id to user id set
			//REDIS SADD
			redis.sadd("user.ids", userid);
		}
		
		
		//What is the cardinality of the user id set?
		//REDIS SCARD
		System.out.println(String.format(
				"Number of unique user IDs: %d",redis.scard("user.ids")
		));
		
		//Get members of the set
		//REDIS SMEMBERS
		System.out.println(redis.smembers("user.ids"));
		
	}
}
