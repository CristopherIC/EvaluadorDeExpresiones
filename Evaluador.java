import java.util.Stack;

public class Evaluador {

	public static void main(String[] args) {
		Evaluador e = new Evaluador();
		e.start();
	}
	
	//GLC
	// Alfabeto = digito, “ + ”, “ - “, “ * ”, “ ( “, “ ) ”
	// S -> digito | S O S | ( S )
	// O -> + | - | * 
		
	Nodo root;
	Nodo current;
	Nodo auxiliar;

	String operacion = "( ( 48 + 25 ) * ( 2 + 4 ) )" ;
	String[] simbolos = operacion.split("\\s+");

	Stack<String> stack = new Stack<String>();


	public void start() {
		this.root = new Nodo("S");
		current=root;

		if(subArbol(current, 0, simbolos.length-1)) {
			System.out.println( operacion + " " +"Es una cadena valida\n");
			preOrderHelper(root);
		} else {
			System.out.println("La cadena no es una expresión aritmetica valida");
		}
	}

	public boolean subArbol(Nodo current, int inicio, int fin) {
		int aux=0;
		int aux2 = 0;
		for(int i=inicio; i<=fin; i++) {

			// Caso 1
			if(i<fin && simbolos[i].equals("(")) {

				stack.push("(");

				aux = i+1;

				while(!simbolos[aux].equals(")") && !simbolos[aux].equals("(")  ) {
					aux++;
				}

				//Caso 1.1
				//Significa que esta bien balanceado
				if(aux==fin && simbolos[aux].equals(")")) {
					current.left = new Nodo("(");
					current.mid = new Nodo("S");
					current.right = new Nodo(")");

					return subArbol(current.mid, i+1, fin);
				}

				//Caso 1.2 
				//Hay que crear un subarbol en la parte derecha
				else if(simbolos[aux].equals(")") && (simbolos[aux+1].equals("+") || simbolos[aux+1].equals("-") || simbolos[aux+1].equals("*"))) {

					current.left = new Nodo("S");	
					current.mid = new Nodo("O");
					current.right = new Nodo("S");

					current.left.left = new Nodo("(");	
					current.left.mid = new Nodo("S");
					current.left.right = new Nodo(")");

					current.mid.left = new Nodo(simbolos[aux+1]);

					//Nodo auxiliar para crear el sub arborl derecho
					auxiliar = current.right;
					aux2 = aux;
					
					if(simbolos[aux+2].equals("(")) {
						stack.push("(");
						current.right.left = new Nodo("(");	
						current.right.mid = new Nodo("S");
						current.right.right = new Nodo(")");

						auxiliar = current.right.mid;
						aux2++;
					}



					if(subArbol(current.left.mid, i+1, aux) && subArbol(auxiliar,aux2+2,fin)) {
						return true;
					} else {
						return false;
					}

				}

				// Caso 1.3
				else if(simbolos[aux].equals(")") && simbolos[aux+1].equals(")")) {

					//Caso 1.3.1
					if(aux+2 < fin && (simbolos[aux+2].equals("+") || simbolos[aux+2].equals("-") || simbolos[aux+2].equals("*"))) {

						/*
						 * 						S
						 *					/	|	\	
						 * 			  root		O		S	
						 * 			  			|			\
						 *       		 	  aux+2		subArbol(aux+3)
						 */

						Nodo temporal = new Nodo("S");
						temporal.left = root;
						temporal.mid = new Nodo("O");
						temporal.right = new Nodo("S");

						temporal.mid.left = new Nodo(simbolos[aux+2]);
						root = temporal;

						subArbol(root.right, aux+3, fin);

					}

					//Caso 1.3.2
					else if(aux+2 < fin && simbolos[aux+2].equals(")")) {
						current.left = new Nodo("(");
						current.mid = new Nodo("S");
						current.right = new Nodo(")");
						//current = current.mid;
						return subArbol(current.mid, i+1, fin);
					} 

					//Casp 1.3.3
					else if(aux+1==fin && simbolos[aux+1].equals(")")) {
						current.left = new Nodo("(");
						current.mid = new Nodo("S");
						current.right = new Nodo(")");
						return subArbol(current.mid, i+1, fin);
					}
					else {
						return false;
					}
				}

				//Caso 1.4
				else if(simbolos[aux].equals("(")) {
					current.left = new Nodo("(");
					current.mid = new Nodo("S");
					current.right = new Nodo(")");
					return subArbol(current.mid, i+1, fin);
				} 

				else {
					return false;
				}
			}

			//Caso 2
			else if(i<fin && isNumeric(simbolos[i])) {
				
				if(simbolos[i+1].equals(")")) {
					
					
					
					
					current.left = new Nodo(simbolos[i]);
					continue;
				} 

				else if(simbolos[i+1].equals("+") || simbolos[i+1].equals("-") || simbolos[i+1].equals("*")) {
					current.left = new Nodo("S");
					current.mid = new Nodo("O");
					current.right = new Nodo("S");

					current.left.left = new Nodo(simbolos[i]);
					current.mid.left = new Nodo(simbolos[i+1]);

					current = current.right;

					//Nos saltamos el analizar el operador pues ya lo hemos puesto
					i++;
					continue;
				}

				//Si no entramos en ninguna de las condiciones entonces la cadena es invalida
				else {
					return false;
				}				
			}

			//Caso 3
			else if(i<fin && simbolos[i].equals(")")) {

				// Si esta vacio, entonces esta mal balanceado
				if(stack.empty()) {				
					return false;
				} 

				else if(stack.peek().equals("(")) {
					stack.pop();
					continue;
				}
			}

			//Caso Final 1 
			else if(i==fin && isNumeric(simbolos[i])) {
				current.left = new Nodo(simbolos[i]);
				return true;
			}

			//Caso Final 2 - Solo puede ocurrir en el ultimo subArbol
			else if(i==simbolos.length-1 && simbolos[i].equals(")")) {

				//Si el ultimo simbolo es un parentesis cerrado pero la pila esta vacia, los parentesis no estaban balanceados
				if(stack.empty()) {				
					return false;
				} 

				else if(stack.peek().equals("(")) {
					stack.pop();
					if(stack.empty()) {
						return true;
					} else {						
						return false;
					}
				} else {
					return false;
				}
			}

			//Caso Final 3
			else if(i==fin && simbolos[i].equals(")")) {

				//Si el ultimo simbolo es un parentesis cerrado pero la pila esta vacia, los parentesis no estaban balanceados
				if(stack.empty()) {				
					return false;
				} 

				else if(stack.peek().equals("(")) {
					stack.pop();
					return true;
				} else {
					return false;
				}
			}			
		}
		return false;
	}

	//Regresa un boolean que indica si el string mandado es un Numero o no
	public static boolean isNumeric(String str) { 
		try {  
			Double.parseDouble(str);
			return true;
		} catch(NumberFormatException e){  
			return false;  
		}  
	}

	public void preOrderHelper(Nodo node) {
		String initialIndent = ""; // Root level has no indentation
		preOrder(initialIndent, node);
	}

	public void preOrder(String indent, Nodo node) {
		if (node == null) {
			return;
		}
		System.out.println(indent + node.value);
		String newIndent = indent + "   ";
		preOrder(newIndent, node.left);
		preOrder(newIndent, node.mid);
		preOrder(newIndent, node.right);
	}




}

class Nodo{

	public String value;
	public Nodo left;
	public Nodo mid;
	public Nodo right;


	Nodo(String value) {
		this.value = value;
		left = null;
		mid=null;
		right = null;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Nodo getLeft() {
		return left;
	}

	public void setLeft(Nodo left) {
		this.left = left;
	}

	public Nodo getRight() {
		return right;
	}

	public void setRight(Nodo right) {
		this.right = right;
	}

	public Nodo getMid() {
		return mid;
	}

	public void setMid(Nodo mid) {
		this.mid = mid;
	}

}
