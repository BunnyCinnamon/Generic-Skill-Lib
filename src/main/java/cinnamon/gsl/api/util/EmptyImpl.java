package cinnamon.gsl.api.util;

import javax.annotation.Nonnull;

public final class EmptyImpl {

    @Nonnull
    public static <T> T mkEmpty() {
        return null;
    }
}
