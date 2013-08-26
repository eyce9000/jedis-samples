package grl.redis.jobs;

import java.util.Set;

import redis.clients.jedis.Jedis;

public class SortedSetSample {
	public static void main(String[] args){
		Jedis redis = new Jedis("localhost");

		redis.set("job.counter","0");
		
		for(int i=0; i<10; i++){
			Long jobid = redis.incr("job.counter");
			String jobData = "{jobid:'"+jobid+"',jobdata:'some data "+jobid+"'}";
			
			//Store job data
			redis.set("job.by.id."+jobid, jobData);
			//Expire in 24 hours
			redis.expire("job.by.id."+jobid, 60*60*24);
			
			//Add job id to ranked processing queue
			//REDIS ZADD
			redis.zadd("jobs.ranked.set", i, jobid+"");
		}
		
		//Get number of jobs to process
		//REDIS ZCARD
		System.out.println(String.format(
				"Jobs to be processed %d",
				redis.zcard("jobs.ranked.set")
		));
		
		//Process all pending jobs in the queue
		//REDIS ZREVRANGEBYSCORE
		Set<String> ids = redis.zrevrangeByScore("jobs.ranked.set", 10, 0);
		for(String pendingJobId:ids){
			String pendingJobData = redis.get("job.by.id."+pendingJobId);
			if(pendingJobData!=null){
				System.out.println(String.format(
						"Processing job %s rank %.0f: data: %s",
						pendingJobId,
						redis.zscore("jobs.ranked.set", pendingJobId),
						pendingJobData
						));
				/*
				 * Do something...
				 */
			}
			
			//Remove from ordered list
			//REDIS ZREM
			redis.zrem("jobs.ranked.set", pendingJobId);
		}
	}
}
