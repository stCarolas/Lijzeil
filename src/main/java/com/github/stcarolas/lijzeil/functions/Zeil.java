package  com.github.stcarolas.lijzeil.functions;

import java.net.URI;

import com.github.stcarolas.lijzeil.Range;
import com.github.stcarolas.lijzeil.WorkspaceChanges;

import io.vavr.Function2;

public interface Zeil extends Function2<URI, Range, WorkspaceChanges> {
	String description();
}
