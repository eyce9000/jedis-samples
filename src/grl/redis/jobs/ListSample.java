package grl.redis.jobs;

import redis.clients.jedis.Jedis;

public class ListSample {
	public static void main(String[] args){
		Jedis redis = new Jedis("localhost");
		
		redis.set("job.counter","0");
		
		for(int i=0; i<10; i++){
			//Get a new id by incrementing the job counter
			//REDIS INCR
			Long jobid = redis.incr("job.counter");
			String jobData = "{jobid:'"+jobid+"',jobdata:'some data "+jobid+"'}";
			
			//Store job data
			redis.set("job.by.id."+jobid, jobData);
			//Expire in 24 hours
			redis.expire("job.by.id."+jobid, 60*60*24);
			
			//Add job id to processing queue
			//REDIS LPUSH
			redis.lpush("jobs.queue", jobid+"");
		}
		
		//Get number of jobs to process
		//REDIS LLEN
		System.out.println(String.format(
				"Jobs to be processed %d",
				redis.llen("jobs.queue")
		));
		
		//Process all pending jobs in the queue
		//REDIS RPOP
		String pendingJobId = null;
		while((pendingJobId = redis.rpop("jobs.queue"))!=null){
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
		}
	}
}
