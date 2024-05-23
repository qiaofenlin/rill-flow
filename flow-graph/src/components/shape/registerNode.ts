import { Graph, Node } from '@antv/x6';
import '@antv/x6-vue-shape';
import NodeTemplate from './NodeTemplate.vue';

export function registerVueNode() {
  Graph.registerVueComponent(
    'vue-node',
    {
      template: `<NodeTemplate />`,
      components: {
        NodeTemplate,
      },
    },
    true,
  );
}

export class NodeGroup extends Node {
  private collapsed = true;

  protected postprocess() {
    this.toggleCollapse(true);
  }

  isCollapsed() {
    return this.collapsed;
  }

  toggleCollapse(collapsed?: boolean) {
    const target = collapsed == null ? !this.collapsed : collapsed;
    if (target) {
      this.attr('buttonSign', { d: 'M 1 5 9 5 M 5 1 5 9' });
      this.resize(200, 40);
    } else {
      this.attr('buttonSign', { d: 'M 2 5 8 5' });
      this.resize(240, 240);
    }
    this.collapsed = target;
  }
}

NodeGroup.config({
  shape: 'rect',
  markup: [
    {
      tagName: 'rect',
      selector: 'body',
    },
    {
      tagName: 'image',
      selector: 'image',
    },
    {
      tagName: 'text',
      selector: 'text',
    },
    {
      tagName: 'g',
      selector: 'buttonGroup',
      children: [
        {
          tagName: 'rect',
          selector: 'button',
          attrs: {
            'pointer-events': 'visiblePainted',
          },
        },
        {
          tagName: 'path',
          selector: 'buttonSign',
          attrs: {
            fill: 'none',
            'pointer-events': 'none',
          },
        },
      ],
    },
  ],
  attrs: {
    body: {
      refWidth: '100%',
      refHeight: '100%',
      strokeWidth: 1,
      fill: 'rgba(95,149,255,0.05)',
      stroke: '#5F95FF',
    },
    image: {
      'xlink:href': 'https://gw.alipayobjects.com/mdn/rms_0b51a4/afts/img/A*X4e0TrDsEiIAAAAAAAAAAAAAARQnAQ',
      width: 16,
      height: 16,
      x: 8,
      y: 12,
    },
    text: {
      fontSize: 12,
      fill: 'rgba(0,0,0,0.85)',
      refX: 30,
      refY: 15,
    },
    buttonGroup: {
      refX: '100%',
      refX2: -25,
      refY: 13,
    },
    button: {
      height: 14,
      width: 16,
      rx: 2,
      ry: 2,
      fill: '#f5f5f5',
      stroke: '#ccc',
      cursor: 'pointer',
      event: 'node:collapse',
    },
    buttonSign: {
      refX: 3,
      refY: 2,
                     stroke: '#808080',
    },
  },
});

Graph.registerNode('groupNode', NodeGroup);


export function registerDefaultNode(graph: Graph) {
  graph.createNode({
    shape: 'groupNode',
    attrs: {
      text: {
        text: 'Group Name',
      },
    },
    data: {
      parent: true,
    },
  });
}
