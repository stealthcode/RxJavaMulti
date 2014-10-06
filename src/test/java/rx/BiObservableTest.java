package rx;

import java.io.File;
import java.util.Objects;

import org.junit.Test;

import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;
import rx.functions.Func2;

public class BiObservableTest {
    @Test
    public void exampleOfUsage() {
        Observable<Stage1> ops = Observable.range(0, 10)
            .map(new Func1<Integer, Stage1>() {
                @Override
                public Stage1 call(Integer t1) {
                    return new Stage1(t1, "args");
                }
            });
        BiObservable<OperationLogger, Stage2> pipeline = BiObservable.from(ops, new Func1<Stage1, OperationLogger>() {
                @Override
                public OperationLogger call(Stage1 t1) {
                    return new OperationLogger(t1.getId());
                }})
            .mapSecond(new Func2<OperationLogger, Stage1, Stage2>() {
                @Override
                public Stage2 call(OperationLogger op, Stage1 t2) {
                    try {
                        op.log("Starting stage1");
                        Stage2 stage2 = Stage2.advance(t2);
                        op.log("Ending stage1");
                        return stage2;
                    } catch (Exception e) {
                        op.logException("Exception in Stage1", e);
                        throw new RuntimeException("Stage1 failed for operation "+t2.getId(), e);
                    }
                }
            });
        pipeline.selectFirst()
            .doOnNext(new Action1<OperationLogger>() {
                @Override
                public void call(OperationLogger t1) {
                    t1.dumpLog(new File("/tmp/pipeline/log.txt"));
                }});
        
        Observable<String> continuation = pipeline
                .selectSecond()
                .map(new Func1<Stage2, String>() {

                    @Override
                    public String call(Stage2 t2) {
                        return t2.getContent();
                    }
                });
        BiObservable.just(new OutputWriter(), continuation)
            .doOnNext(new Action2<OutputWriter, String>() {

                @Override
                public void call(OutputWriter t1, String t2) {
                    t1.write(t2);
                }
            }).subcribe(new DualSubscriber<OutputWriter, String>() {

                @Override
                public void onNext(OutputWriter t0, String t1) {
                    Objects.requireNonNull(t0);
                    Objects.requireNonNull(t1);
                    System.out.println("Finished operation");
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
                }});;
    }

    private static class Stage1 {
        private final int id;
        private final String content;

        public Stage1(int id, String content) {
            this.id = id;
            this.content = content;
        }

        public int getId() {
            return id;
        }

        public String getContent() {
            return content;
        }
    }
    
    private static class Stage2 {
        private final int id;
        private final String content;

        public Stage2(int id, String content) {
            this.id = id;
            this.content = content;
        }

        private static Stage2 advance(Stage1 stage1) throws Exception {
            if (System.currentTimeMillis() % 3 == 0)
                throw new Exception("Unlucky roll. Tough break pal.");
            int id = stage1.getId();
            String content = "("+ stage1.getContent() + ")";
            return new Stage2(id, content);
        }
        
        public String getContent() {
            return content;
        }
    }
    
    private static class OperationLogger {
        public OperationLogger(int operationId) {
            log("Starting log for operation "+ operationId);
        }
        
        public void dumpLog(File f) {
            System.out.println(log.toString());
        }

        private static class LogBuilder {
            StringBuilder s = new StringBuilder();
            public LogBuilder append(String msg) {
                s.append(msg).append('\n');
                return this;
            }
        }
        
        LogBuilder log = new LogBuilder();
        private boolean failed = false;
        public void log(String msg) {
            log.append(msg);
        }
        
        public void logException(String msg, Exception e) {
            log(msg);
            log(e.getMessage());
            // do other fancy things to coerce the exception to be a string
        }
        
        public void logFailure(String msg) {
            failed = true;
            log(msg);
        }

        public boolean hasFailed() {
            return failed;
        }
    }
    
    private static class OutputWriter {
        public void write(String data) {
            System.out.println("Output Writer:\n" + data);
        }
    }
}    
