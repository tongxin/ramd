package ramd;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;

public class Futures {

    private List<Future> _fs = new ArrayList<Future>();

    synchronized public final Futures add(Future f) {
        if (f != null) _fs.add(f);
        return this;
    }

    synchronized public final Futures update() {
        for (int i = 0; i < _fs.size(); i++) {
            if (_fs.get(i).isDone())
                _fs.set(i--, _fs.remove(_fs.size() - 1));
        }
        return this;
    }

    public final void block() throws Exception {
        while (!_fs.isEmpty()) {
            Future f;
            synchronized (this) {
                if (_fs.isEmpty())
                    return;

                f = _fs.remove(_fs.size() - 1);
            }

            try {
                f.get();
            } catch (CancellationException e) {}
        }
    }
}
