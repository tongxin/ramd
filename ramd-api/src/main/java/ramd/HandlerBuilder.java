package ramd;

public interface HandlerBuilder<Handler> {
    public Handler build() throws Exception;
}
