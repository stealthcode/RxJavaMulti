package rx;

import org.junit.Test;

import rx.functions.Func1;
import rx.functions.Func2;

import java.io.File;
import java.util.Objects;

public class BiObservableTest {
    @Test
    public void exampleOfUsage() {
        Observable<Stage1> ops = Observable.range(0, 10).map(new Func1<Integer, Stage1>() {
            @Override
            public Stage1 call(Integer id) {
                return new Stage1(id, "args");
            }
        });
        BiObservable.zip(ops, new Func1<Stage1, OperationLogger>() {
            @Override
            public OperationLogger call(Stage1 stage1) {
                return new OperationLogger(stage1.getId());
            }
        }).mapFirst(new Func2<Stage1, OperationLogger, Stage2>() {
            @Override
            public Stage2 call(Stage1 stage1, OperationLogger logger) {
                try {
                    logger.log("Starting stage1");
                    Stage2 stage2 = Stage2.advance(stage1);
                    logger.log("Ending stage1");
                    return stage2;
                } catch (Exception e) {
                    logger.logException("Exception in Stage1", e);
                    throw new RuntimeException("Stage1 failed for operation " + stage1.getId(), e);
                }
            }
        }).mapSecond(new Func1<OperationLogger, OutputWriter>() {
            @Override
            public OutputWriter call(OperationLogger logger) {
                logger.dumpLog(new File("/tmp/pipeline/log.txt"));
                return new OutputWriter();
            }
        }).mapFirst(new Func2<Stage2, OutputWriter, String>() {
            @Override
            public String call(Stage2 stage2, OutputWriter writer) {
                String content = stage2.getContent();
                writer.write(content);
                return content;
            }
        }).subcribe(new DualSubscriber<String, OutputWriter>() {
            @Override
            public void onNext(String content, OutputWriter writer) {
                Objects.requireNonNull(writer);
                Objects.requireNonNull(content);
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
            }
        });
        ;
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
            /*
            if (System.currentTimeMillis() % 3 == 0)
                throw new Exception("Unlucky roll. Tough break pal.");
                */
            int id = stage1.getId();
            String content = "(" + stage1.getContent() + ")";
            return new Stage2(id, content);
        }

        public String getContent() {
            return content;
        }
    }

    private static class OperationLogger {
        public OperationLogger(int operationId) {
            log("Starting log for operation " + operationId);
        }

        public void dumpLog(File f) {
            System.out.println(log.getLog());
        }

        private static class LogBuilder {
            StringBuilder s = new StringBuilder();

            public LogBuilder append(String msg) {
                s.append(msg).append('\n');
                return this;
            }

            public String getLog() {
                return s.toString();
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
