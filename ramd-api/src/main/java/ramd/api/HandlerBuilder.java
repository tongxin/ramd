package ramd.api;

public interface HandlerBuilder<Handler> {
    public Handler build() throws Exception;
}
