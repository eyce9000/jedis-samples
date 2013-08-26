package grl.redis.jobs;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

public class SubscriberSample {
	public static void main(String[] args){
		final JedisPool redisPool = new JedisPool(new JedisPoolConfig(), "localhost");
		Thread subscriberThread = new Thread(new Runnable(){
			public void run(){
				Jedis subscriberRedis = redisPool.getResource(); 
				subscriberRedis.subscribe(new JedisPubSub(){
		
					@Override
					public void onMessage(String channel, String message) {
						Jedis redis = redisPool.getResource();
						String pendingJobId = message;
						String pendingJobData = redis.get("job.by.id."+pendingJobId);
						if(pendingJobData!=null){
							System.out.println(String.format(
									"Processing job %s: data: %s",
									pendingJobId,
									pendingJobData
									));
							/*
							 * Do something...
							 */
						}
						redisPool.returnResource(redis);
					}
		
					@Override
					public void onPMessage(String pattern, String channel,String message) {}
		
					@Override
					public void onSubscribe(String channel, int subscribedChannels) {}
		
					@Override
					public void onUnsubscribe(String channel, int subscribedChannels) {}
		
					@Override
					public void onPUnsubscribe(String pattern, int subscribedChannels) {}
		
					@Override
					public void onPSubscribe(String pattern, int subscribedChannels) {}
					
				}, "jobs.channel");
			}
		});
		
		subscriberThread.start();
		
		Jedis publisherRedis = redisPool.getResource();
		publisherRedis.set("job.counter","0");
		for(int i=0; i<10; i++){
			Long jobid = publisherRedis.incr("job.counter");
			String jobData = "{jobid:'"+jobid+"',jobdata:'some data "+jobid+"'}";
			
			//Store job data
			publisherRedis.set("job.by.id."+jobid, jobData);
			//Expire in 24 hours
			publisherRedis.expire("job.by.id."+jobid, 60*60*24);
			
			//Send the job id to subscribers
			//REDIS PUBLISH
			publisherRedis.publish("jobs.channel", jobid+"");
		}
	}
	
	
}
