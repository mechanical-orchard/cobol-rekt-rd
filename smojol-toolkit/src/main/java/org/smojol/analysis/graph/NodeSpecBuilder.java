package org.smojol.analysis.graph;

import com.google.common.collect.ImmutableList;
import com.mojo.woof.NodeSpec;
import org.smojol.common.flowchart.FlowNode;
import org.smojol.common.flowchart.NodeText;
import org.smojol.common.id.IdProvider;
import org.smojol.common.id.UUIDProvider;
import org.smojol.common.vm.structure.CobolDataStructure;

import java.util.HashMap;
import java.util.Map;

import static com.mojo.woof.NodeLabels.*;
import static com.mojo.woof.NodeProperties.*;
import static com.mojo.woof.NodeProperties.LEVEL;

public class NodeSpecBuilder {
    private final NamespaceQualifier namespaceQualifier;
    private final IdProvider idProvider;

    public NodeSpecBuilder(NamespaceQualifier namespaceQualifier) {
        this(namespaceQualifier, new UUIDProvider());
    }

    public NodeSpecBuilder(NamespaceQualifier namespaceQualifier, IdProvider idProvider) {
        this.namespaceQualifier = namespaceQualifier;
        this.idProvider = idProvider;
    }

    public NodeSpec newDataNode(CobolDataStructure structure) {
        return new NodeSpec(ImmutableList.of(DATA_STRUCTURE, structure.getDataType().toString()),
                Map.of(ID, idProvider.next(),
                        INTERNAL_ID, structure.getId(),
                        NAME, structure.name(),
                        TEXT, structure.content(),
                        TYPE, structure.getDataType().toString(),
                        ENTITY_TYPE, DATA_STRUCTURE,
                        LEVEL, structure.getLevelNumber(),
                        NAMESPACE, namespaceQualifier.getNamespace()
                ));
    }

    public NodeSpec newASTNode(FlowNode node) {
        return labelledCodeNode(node, AST_NODE);
    }

    public NodeSpec newCFGNode(FlowNode node) {
        return labelledCodeNode(node, CFG_NODE);
    }

    public NodeSpec labelledCodeNode(FlowNode node, String nodeType) {
        return new NodeSpec(ImmutableList.of(nodeType, node.type().toString()),
                Map.of(ID, idProvider.next(),
                        INTERNAL_ID, node.id(),
                        NAME, node.name(),
                        TEXT, NodeText.originalText(node.getExecutionContext(), NodeText::PASSTHROUGH),
                        TYPE, node.type().toString(),
                        ENTITY_TYPE, nodeType,
                        NAMESPACE, namespaceQualifier.getNamespace()
                ));
    }

    public NodeSpec newTraceNode(FlowNode node) {
        return new NodeSpec(ImmutableList.of(CFG_TRACE, node.type().toString()),
                Map.of(ID, idProvider.next(),
                        INTERNAL_ID, node.id(),
                        NAME, node.name(),
                        TEXT, node.getExecutionContext().getText(),
                        TYPE, node.type().toString(),
                        ENTITY_TYPE, CFG_TRACE,
                        NAMESPACE, namespaceQualifier.getNamespace()
                ));
    }

    public NodeSpec dataNodeSearchSpec(CobolDataStructure structure) {
        return dataNodeSearchCriteria(Map.of(INTERNAL_ID, structure.getId(), NAME, structure.name(), NAMESPACE, namespaceQualifier.getNamespace()));
    }

    public NodeSpec cfgNodeSearchSpec(FlowNode node) {
        return labelledNodeSearchSpec(node, CFG_NODE);
    }

    public NodeSpec astNodeSearchSpec(FlowNode node) {
        return labelledNodeSearchSpec(node, AST_NODE);
    }

    public NodeSpec labelledNodeSearchSpec(FlowNode node, String nodeType) {
        return new NodeSpec(ImmutableList.of(nodeType), Map.of(INTERNAL_ID, node.id(), ENTITY_TYPE, nodeType, NAMESPACE, namespaceQualifier.getNamespace()));
    }

    public NodeSpec dataNodeSearchCriteria(Map<String, Object> criteria) {
        HashMap<String, Object> finalCriteria = new HashMap<>();
        finalCriteria.put(NAMESPACE, namespaceQualifier.getNamespace());
        finalCriteria.putAll(criteria);
        return new NodeSpec(ImmutableList.of(DATA_STRUCTURE), finalCriteria);
    }

    public NodeSpec astNodeCriteria(Map<String, Object> criteria) {
        HashMap<String, Object> finalCriteria = new HashMap<>();
        finalCriteria.put(NAMESPACE, namespaceQualifier.getNamespace());
        finalCriteria.putAll(criteria);
        return new NodeSpec(ImmutableList.of(AST_NODE), finalCriteria);
    }
}
