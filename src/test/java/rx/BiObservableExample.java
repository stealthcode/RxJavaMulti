package rx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Test;

public class BiObservableExample {
    @Test
    public void exampleOfUsage() {
        // A service for fetching persistent streams of jobs
        JobService jobService = new JobService();
        // A service for serializing output to subscribed 
        OutputService outputService = new OutputService();
        
        Observable<JobRequest> jobRequests = jobService.getNewJobRequests("foo");

        // Runs a job for each jobRequest and converts to a string of jobResults
        Observable<String> jobResults = BiObservable
                .generate(jobRequests, (JobRequest jobRequest) -> {
                    return findJob(jobRequest);})
                .map2((JobRequest jobRequest, Job job) -> {
                    return job.doWork(jobRequest.getParameters());
                })
                .bimap((JobRequest jobRequest, String results) -> {
                    return "JobId: " + jobRequest.getId() + "\n" +
                            "Parameters: " + jobRequest.getParameters().toString() + "\n" +
                            "Results:\n" + results;
                });
        
        // A list of zero or more OutputWriters
        Observable<OutputWriter> outputSubscribers = outputService.getOutputSubscribers();
        
        // For each jobResult emitted and each outputSubscriber (cached to prevent resubscribing)
        BiObservable.product(jobResults, outputSubscribers.cache())
                .doOnNext((String result, OutputWriter writer) -> {
                        System.out.println("Finished job");
                        writer.write(result);
                    })
                .subcribe(new BiSubscriber<String, OutputWriter>() {
                    @Override
                    public void onNext(String result, OutputWriter writer) {
                        Objects.requireNonNull(writer);
                        Objects.requireNonNull(result);
                        System.out.println("Published job results to subscribed writer: " + writer.getName());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Objects.requireNonNull(e);
                        System.out.println("Error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Completed pipeline");
                    }
                });
    }
    
    public Job findJob(JobRequest jobRequest) {
        try {
            return jobMap.get(jobRequest.getJobName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to find job for request id:" + jobRequest.getId(), e);
        }
    }
    
    @SuppressWarnings("serial")
    private final Map<String, Job> jobMap = new HashMap<String, Job>() {{
        put("foo", new Job() {

            @Override
            public String doWork(List<String> parameters) {
                return "Okay";
            }});
    }};

    private static class JobRequest {
        private final int id;
        private final String jobName;
        private final List<String> parameters;

        public JobRequest(int id, String jobName, List<String> parameters) {
            this.id = id;
            this.jobName = jobName;
            this.parameters = parameters;
        }

        public int getId() {
            return id;
        }

        public List<String> getParameters() {
            return parameters;
        }

        public String getJobName() {
            return jobName;
        }
    }
    
    private abstract static class Job {
        public abstract String doWork(List<String> parameters);
    }
    
    private static class JobService {
        private Map<String, List<String>> parameterMap = new HashMap<String, List<String>>();
        public JobService() {
            parameterMap.put("foo", Arrays.asList(new String[]{"-bar", "-qux"}));
        }

        public Observable<JobRequest> getNewJobRequests(String jobName) {
            List<String> standardParameters = getStandardParameters(jobName);
            return BiObservable.attach(Observable.range(0, 10), standardParameters)
                    .bimap((id, defaultParameters) -> new JobRequest(id, jobName, defaultParameters));
        }

        private List<String> getStandardParameters(String jobName) {
            return new ArrayList<String>(parameterMap.get(jobName));
        }
    }

    private static class OutputService {
        public Observable<OutputWriter> getOutputSubscribers() {
            // this should normally be generated from a pool of output workers or something sophisticated
            return Observable.just(new OutputWriter("Pub/Sub Integration")); 
        }
    }
    
    private static class OutputWriter {
        private String name;

        public OutputWriter(String name) {
            this.name = name;
            
        }
        
        public void write(String data) {
            System.out.println("Output Writer:\n" + data);
        }

        public String getName() {
            return name;
        }
    }
}
