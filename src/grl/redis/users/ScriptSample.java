package grl.redis.users;

import redis.clients.jedis.Jedis;

public class ScriptSample {
	public static void main(String[] args) throws InterruptedException{
		Jedis redis = new Jedis("localhost");
		
		String userid = "12356";
		//Expires in 1 second
		redis.expire("user.by.id."+userid,1);
		Thread.currentThread().sleep(2000);
		String lua = 
				"local ids = redis.call(\"smembers\",\"user.ids\")\n"+
				"local cleanedCount = 0\n"+
				"for i, id in ipairs(ids) do\n"+
				"	local exists = redis.call(\"exists\",\"user.by.id.\"..id)\n"+
				"	if exists ~= 1 then\n"+
				"		redis.call(\"srem\",\"user.ids\",id)\n"+
				"		cleanedCount = cleanedCount + 1\n"+
				"	end\n"+
				"end\n"+
				"return cleanedCount\n";

		long cleanedCount = (Long)redis.eval(lua);
		
		System.out.println(String.format(
				"Removed %d user ids from the user.ids set",
				cleanedCount
		));
	}
}
