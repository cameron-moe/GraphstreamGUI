import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.algorithm.Kruskal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

// Cameron Moe
// CSC 3430 GraphStream Project
// 3/10/19
// Uses the GraphStream Library to create a GUI interface where user can select
// minimum spanning tree or shortest path between points, as well as clearing operations

public class adaa {
	public static void main(String args[]) throws IOException {
		EventQueue.invokeLater(() -> {
			try {
				new adaa().display(args[0]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});

	}

	private void display(String readFile) throws FileNotFoundException {

		// graph creation section
		Graph graph = new MultiGraph("graph");

		String file = readFile;
		Scanner scanner = new Scanner(new File(file));
		String node1;
		String node2;
		int weight = 0;
		// stores nodes (used later)
		ArrayList<String> nodes = new ArrayList<String>();
		while (scanner.hasNext()) {
			node1 = scanner.next();
			node2 = scanner.next();
			weight = scanner.nextInt();
			// creates nodes if not created yet
			if (!nodes.contains(node1)) {
				graph.addNode(node1);
				nodes.add(node1);
			}
			if (!nodes.contains(node2)) {
				graph.addNode(node2);
				nodes.add(node2);
			}
			// adds edge with assigned weight from file
			graph.addEdge(node1 + node2, node1, node2).addAttribute("weight", weight);
		}
		scanner.close();

		// JFrame/Panel used for GUI
		JFrame frame = new JFrame();
		frame.setSize(300, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel(new GridBagLayout()) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(640, 480);
			}
		};
		panel.setBorder(BorderFactory.createLineBorder(Color.blue, 5));

		// prints node names and edge labels
		for (Node n : graph)
			n.addAttribute("label", n.getId());
		for (Edge e : graph.getEachEdge())
			e.addAttribute("label", "" + (int) e.getNumber("weight"));

		// custom jpanel allows buttons on sides and top and graph in middle
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// natural height, maximum width

		JTextField jt = new JTextField(30);
		JButton jb = new JButton("Enter");
		JLabel jl = new JLabel("Shortest Path Between Two Nodes, Enter as: a,b");

		// places textentry in the top center "grid"
		c.ipady = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		panel.add(jt, c);

		jt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = jt.getText();
			}
		});

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 2;
		c.gridy = 0;
		panel.add(jb, c);
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String input = jt.getText();
				if (input.length() == 3) {

					// checks for properly formatted entry
					for (Node node : graph.getNodeSet())
						node.addAttribute("ui.style", "fill-color: black;");
					for (Edge edge : graph.getEdgeSet())
						edge.addAttribute("ui.style", "fill-color: black;");

					// calculates start and finishing nodes
					char first = input.charAt(0);
					int nodeToGet = first - 97;

					char last = input.charAt(2);
					int nodeToGet2 = last - 97;
					// colors shortest path
					Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "weight");

					// uses nodes arraylist from before (for some reason it
					// didn't like just using substring)
					dijkstra.init(graph);
					dijkstra.setSource(graph.getNode(nodes.get(nodeToGet)));
					dijkstra.compute();

					for (Node node : dijkstra.getPathNodes(graph.getNode(nodes.get(nodeToGet2))))
						node.addAttribute("ui.style", "fill-color: blue;");

					for (Edge edge : dijkstra.getPathEdges(graph.getNode(nodes.get(nodeToGet2))))
						edge.addAttribute("ui.style", "fill-color: blue;");
				}
			}
		});

		// for label for shortest path
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(jl, c);

		// mimimum spanning tree button
		JButton mst = new JButton("Minimum Spanning Tree");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 2;
		panel.add(mst, c);
		mst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// colors minimum spanning tree

				for (Node node : graph.getNodeSet())
					node.addAttribute("ui.style", "fill-color: black;");

				// uses kruskals algorithm for the minimum spanning tree
				String css = "edge .notintree {size:1px;fill-color:black;} "
						+ "edge .intree {size:3px;fill-color:red;}";
				graph.addAttribute("ui.stylesheet", css);
				Kruskal kruskal = new Kruskal("ui.class", "intree", "notintree");
				kruskal.init(graph);
				kruskal.compute();
			}
		});

		// clear button at bottom right
		JButton clear = new JButton("Clear");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 2;
		c.gridy = 2;
		panel.add(clear, c);
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// removes styling done by kruskal, sets everything back to
				// black
				graph.removeAttribute("ui.stylesheet");
				for (Node node : graph.getNodeSet())
					node.addAttribute("ui.style", "fill-color: black;");
				for (Edge edge : graph.getEdgeSet())
					edge.addAttribute("ui.style", "fill-color: black;");
			}
		});

		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 375; // make this component tall
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 1;

		// graph in the middle, uses a viewer to place it in the panel

		Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
		ViewPanel viewPanel = viewer.addDefaultView(false);
		viewPanel.setPreferredSize(new Dimension(200, 200));
		panel.add(viewPanel, c);
		viewer.enableAutoLayout();
		
		// adds panel to frame, shows everything
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
