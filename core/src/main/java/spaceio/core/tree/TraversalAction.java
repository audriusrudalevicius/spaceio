package spaceio.core.tree;

public interface TraversalAction<T extends TreeNode> {

    /**
     * Is called on each node, while traversing the tree
     *
     * @param node reference to the current node during tree traversal
     */
    void perform(T node);

    /**
     * Checks whether the traversal is completed and no more required
     *
     * @return {@code true} if traversal is completed and no more required,
     *         {@code false} otherwise
     */
    boolean isCompleted();

}