package laboratorio5;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
public class arbol_AVL {
	NodoAVL root;
	Graph graph;
	public arbol_AVL(){
		this.graph=new SingleGraph("grafico");
	}
	public void clearAll() {
		root = null;
	}
	
	// para poder vizualizar el Graphstream
	public void visualizacion() {
        visualice(root);
        graph.setAttribute("ui.stylesheet", "node { shape: circle; size: 30px; fill-color: #9FD4FF; text-alignment: center; text-style: bold; }"
                + "edge { fill-color: #555; size: 2px; }");
        graph.display();
    }
	private void visualice(NodoAVL node) {
        if (node != null) {
            graph.addNode(String.valueOf(node.key)).setAttribute("ui.label", String.valueOf(node.key));
            if (node.left != null) {
                visualice(node.left);
                graph.addEdge(String.valueOf(node.key) + "-" + String.valueOf(node.left.key),
                        String.valueOf(node.key), String.valueOf(node.left.key));
            }
            if (node.right != null) {
                visualice(node.right);
                graph.addEdge(String.valueOf(node.key) + "-" + String.valueOf(node.right.key),
                        String.valueOf(node.key), String.valueOf(node.right.key));
            }
        }
	}
	
	
	//insercion 

	public void insert(int key) {
		root = insertarAVL(root, key);
	}

	private NodoAVL insertarAVL(NodoAVL nodoActual, int key) {
		if (nodoActual == null) {
			return (new NodoAVL(key));
		}

		if (key < nodoActual.key) {
			nodoActual.left = insertarAVL(nodoActual.left, key);
		} else if (key > nodoActual.key) {
			nodoActual.right = insertarAVL(nodoActual.right, key);
		} else {// Si la clave esta duplicada retorna el mismo nodo encontrado
			return nodoActual;
		}

		// Actualizacion de la altura
		nodoActual.altura = 1 +
				max(getAltura(nodoActual.left), getAltura(nodoActual.right));

		// Se obtiene el factor de equilibrio
		int fe = getFactorEquilibrio(nodoActual);

		
		if (fe > 1 && key < nodoActual.left.key) {
			return rightRotate(nodoActual);
		}

	
		if (fe < -1 && key > nodoActual.right.key) {
			return leftRotate(nodoActual);
		}

		
		if (fe > 1 && key > nodoActual.left.key) {
			nodoActual.left = leftRotate(nodoActual.left);
			return rightRotate(nodoActual);
		}

		
		if (fe < -1 && key < nodoActual.right.key) {
			nodoActual.right = rightRotate(nodoActual.right);
			return leftRotate(nodoActual);
		}

		return nodoActual;
	}

	// busqueda

	// ---busqueda de un elemento en el AVL
	public void search(int elemento) {
		if (BuscaEnAVL(root, elemento)) {
			System.out.println("Existe");
		} else {
			System.out.println("No Existe");
		}
	}

	// Busqueda recursiva en un AVL
	private boolean BuscaEnAVL(NodoAVL nodoActual, int elemento) {
		if (nodoActual == null) {
			return false;
		} else if (elemento == nodoActual.key) {
			return true;
		} else if (elemento < nodoActual.key) {
			return BuscaEnAVL(nodoActual.left, elemento);
		} else {
			return BuscaEnAVL(nodoActual.right, elemento);
		}
	}
	// eliminacion
	public void remove(int key) {
		root = eliminarAVL(root, key);
	}

	private NodoAVL eliminarAVL(NodoAVL nodoActual, int key) {
		if (nodoActual == null)
			return nodoActual;

		if (key < nodoActual.key) {
			nodoActual.left = eliminarAVL(nodoActual.left, key);
		} else if (key > nodoActual.key) {
			nodoActual.right = eliminarAVL(nodoActual.right, key);
		} else {
			// El nodo es igual a la clave, se elimina
			// Nodo con un unico hijo o es hoja
			if ((nodoActual.left == null) || (nodoActual.right == null)) {
				NodoAVL temp = null;
				if (temp == nodoActual.left) {
					temp = nodoActual.right;
				} else {
					temp = nodoActual.left;
				}

				// Caso que no tiene hijos
				if (temp == null) {
					nodoActual = null;// Se elimina dejandolo en null
				} else {
					// Caso con un hijo
					nodoActual = temp;// Elimina el valor actual reemplazandolo por su hijo
				}
			} else {
				// Nodo con dos hijos, se busca el predecesor
				NodoAVL temp = getNodoConValorMaximo(nodoActual.left);

				// Se copia el dato del predecesor
				nodoActual.key = temp.key;

				// Se elimina el predecesor
				nodoActual.left = eliminarAVL(nodoActual.left, temp.key);
			}
		}

		// Si solo tiene un nodo
		if (nodoActual == null)
			return nodoActual;

		// Actualiza altura
		nodoActual.altura = max(getAltura(nodoActual.left), getAltura(nodoActual.right)) + 1;

		// Calcula factor de equilibrio (FE)
		int fe = getFactorEquilibrio(nodoActual);

		if (fe > 1 && getFactorEquilibrio(nodoActual.left) >= 0) {
			return rightRotate(nodoActual);
		}
		if (fe < -1 && getFactorEquilibrio(nodoActual.right) <= 0) {
			return leftRotate(nodoActual);
		}

		if (fe > 1 && getFactorEquilibrio(nodoActual.left) < 0) {
			nodoActual.left = leftRotate(nodoActual.left);
			return rightRotate(nodoActual);
		}

		if (fe < -1 && getFactorEquilibrio(nodoActual.right) > 0) {
			nodoActual.right = rightRotate(nodoActual.right);
			return leftRotate(nodoActual);
		}

		return nodoActual;
	}

	//rotaciones 
	
	private NodoAVL rightRotate(NodoAVL nodoActual) {
		NodoAVL nuevaRaiz = nodoActual.left;
		NodoAVL temp = nuevaRaiz.right;

		// Se realiza la rotacion
		nuevaRaiz.right = nodoActual;
		nodoActual.left = temp;

		// Actualiza alturas
		nodoActual.altura = max(getAltura(nodoActual.left), getAltura(nodoActual.right)) + 1;
		nuevaRaiz.altura = max(getAltura(nuevaRaiz.left), getAltura(nuevaRaiz.right)) + 1;

		return nuevaRaiz;
	}

	// Rotar hacia la izquierda
	private NodoAVL leftRotate(NodoAVL nodoActual) {
		NodoAVL nuevaRaiz = nodoActual.right;
		NodoAVL temp = nuevaRaiz.left;

		// Se realiza la rotacion
		nuevaRaiz.left = nodoActual;
		nodoActual.right = temp;

		// Actualiza alturas
		nodoActual.altura = max(getAltura(nodoActual.left), getAltura(nodoActual.right)) + 1;
		nuevaRaiz.altura = max(getAltura(nuevaRaiz.left), getAltura(nuevaRaiz.right)) + 1;

		return nuevaRaiz;
	}

	//mostrar

	public void mostrarArbolAVL() {
		System.out.println("Arbol AVL");
		showTree(root, 0);
	}

	private void showTree(NodoAVL nodo, int depth) {
		if (nodo.right != null) {
			showTree(nodo.right, depth + 1);
		}
		for (int i = 0; i < depth; i++) {
			System.out.print("    ");
		}
		System.out.println("(" + nodo.key + ")");

		if (nodo.left != null) {
			showTree(nodo.left, depth + 1);
		}
	}
	private int getAltura(NodoAVL nodoActual) {
		if (nodoActual == null) {
			return 0;
		}
		return nodoActual.altura;
	}

	// Devuelve el mayor entre dos numeros
	private int max(int a, int b) {
		return (a > b) ? a : b;
	}

	// Obtiene el factor de equilibrio de un nodo
	private int getFactorEquilibrio(NodoAVL nodoActual) {
		if (nodoActual == null) {
			return 0;
		}

		return getAltura(nodoActual.left) - getAltura(nodoActual.right);
	}

	private NodoAVL getNodoConValorMaximo(NodoAVL node) {
		NodoAVL current = node;

		while (current.right != null) {
			current = current.right;
		}

		return current;
	}
	// este metodo devolvera el nodo con el valor minimo del arbol AVL
	 public NodoAVL getMin() {
	        return getMinHelper(root);
	    }

	    private NodoAVL getMinHelper(NodoAVL root) {
	        if (root == null || root.left == null)
	            return root;

	        return getMinHelper(root.left);
	    }
	    //este metodo devuelve el nodo con valor maximo  del arbol AVL
	    public NodoAVL getMax() {
	        return getMaxHelper(root);
	    }

	    private NodoAVL getMaxHelper(NodoAVL root) {
	        if (root == null || root.right == null)
	            return root;

	        return getMaxHelper(root.right);
	    }
	    // este metodo busca el nodo padre de u nodo  hijo con la clave dada 
	    public NodoAVL parent(int key) {
	        return parentHelper(root, key, null);
	    }

	    private NodoAVL parentHelper(NodoAVL root, int key, NodoAVL parent) {
	        if (root == null)
	            return null;

	        if (root.key == key)
	            return parent;

	        if (key < root.key)
	            return parentHelper(root.left, key, root);
	        else
	            return parentHelper(root.right, key, root);
	    }
	    // esto hace que se vea si un nodo con la clave dada es hijo derecho o izquierdo 
	    public boolean son(int key, int value) {
	        NodoAVL parent = parent(key);

	        if (parent == null)
	            return false;

	        if (parent.left != null && parent.left.key == value)
	            return true;

	        if (parent.right != null && parent.right.key == value)
	            return true;

	        return false;
	    }

	public static void main(String[] args) {
        arbol_AVL arbol = new arbol_AVL();
        System.out.println("EJEMPLO 1");
        arbol.insert(4);
        arbol.insert(2);
        arbol.insert(5);
        arbol.insert(1);
        arbol.insert(3);
        arbol.insert(6);
        arbol.insert(7);
        arbol.mostrarArbolAVL();
        arbol.remove(5);
        arbol.mostrarArbolAVL();
        arbol.visualizacion();
        arbol.search(2);
  
}
}