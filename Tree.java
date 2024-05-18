import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

// Classe Component 
abstract class FileSystemComponent {
    protected String name;

    public FileSystemComponent(String name) {
        this.name = name;
    }

    public abstract void print(String prefix);
}

// Classe Feuille
class FileLeaf extends FileSystemComponent {
    public FileLeaf(String name) {
        super(name);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "|-- " + name);
    }
}

// Composite
class DirectoryComposite extends FileSystemComponent {
    private List<FileSystemComponent> children = new ArrayList<>();

    public DirectoryComposite(String name) {
        super(name);
    }

    public void add(FileSystemComponent component) {
        children.add(component);
    }

    public void remove(FileSystemComponent component) {
        children.remove(component);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "+-- " + name);
        for (FileSystemComponent component : children) {
            component.print(prefix + "   ");
        }
    }
}

// Client
public class Tree {
    public static void main(String[] args) {
        Path rootPath = Paths.get(System.getProperty("user.home"));
        int maxDepth = 3;  // Set the desired maximum depth
        DirectoryComposite root = new DirectoryComposite(rootPath.getFileName().toString());
        try {
            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                private int currentDepth = 0;

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (currentDepth > maxDepth) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    if (!dir.equals(rootPath)) {
                        DirectoryComposite directory = new DirectoryComposite(dir.getFileName().toString());
                        root.add(directory);
                        currentDepth++;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    root.add(new FileLeaf(file.getFileName().toString()));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    currentDepth--;
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    // Ignore errors and continue
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        root.print("");
    }
}
