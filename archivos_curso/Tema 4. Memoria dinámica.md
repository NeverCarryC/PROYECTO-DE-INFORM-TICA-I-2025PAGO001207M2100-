# 1. Presentación
En esta Unidad del curso analizaremos aspectos relativos a la **gestión eficiente de la memoria**, dando respuesta a múltiples cuestiones prácticas:
-  ¿Cómo proteger nuestro código de posibles **corrupciones** de la **pila**?
- ¿Cuándo preferir la **memoria libre** frente a la **pila** para el almacenamiento de variables?
- ¿Qué impacto tiene sobre la eficiencia de nuestro programa la **fragmentación** de la **memoria libre**?
- ¿Cómo conseguir un código robusto que impida la **fuga de recursos**?


**关于内存管理的 我很不理解**
**对于指针，我理解**


# 2. Espacio de memoria virtual
En un sistema Linux, todo **proceso** tiene asociado un **espacio de memoria virtual** gestionado por el **sistema operativo** en cooperación con **hardware específico de la CPU** (concretamente, la unidad de manejo de memoria o **MMU** en sus siglas inglesas).

La arquitectura **x86-64** define un espacio virtual con direcciones de 64 bits, de los cuales las implementaciones actuales habilitan solo los **48** bits *menos significativos*[^1] para direccionamiento.

- tamaño máximo de 256 TiB (2^48 bytes) de espacio virtual de la arquitectura  x64
- de un máximo teórico de 16 EiB (2^64 bytes).
- 4 GiB (2^32 bytes) de la arquitectura x86


![[Pasted image 20251015150714.png]]

![[Pasted image 20251015150730.png]]

# 3. Paginación(difícil)
El espacio de memoria virtual se divide en **distintas** páginas virtuales; a la vez que la **memoria física** se divide en marcos de página de **igual** tamaño.

El sistema operativo transfiere entonces las páginas del proceso a los marcos de página libres en memoria principal según sea necesario, siguiéndose un estricto control en la asignación de las direcciones de memoria física.

Múltiples procesos (muchos de los cuales pueden ser más extensos que la propia memoria física) pueden ejecutarse de esta forma simultáneamente.

[^1]: **48 bits menos significativos**  
	👉 Los “bits menos significativos” son los de la **parte derecha** de la secuencia binaria (los que cambian más rápido).

# 4.  Pila[^2] del usuario (user stack)
En un procesador x86-64, el **registro %rsp** (puntero de pila o stack pointer en inglés) referencia en todo momento a la cabecera de la pila del usuario. Para **alojar (desalojar) memoria**[^3] en la misma, basta disminuir (incrementar) el valor del puntero.


La **llamada a una función** introduce, por lo general, **un nuevo marco de pila** (stack frame en inglés) en la pila del usuario, **disminuyendo** el valor del registro %rsp convenientemente[^4]. 

Así, supongamos que una función f() invoca a otra función g() (véase la imagen III). En primer lugar, **se introduce** en el marco de f() **la dirección de retorno donde el programa debe continuar su ejecución** tras finalizar g().

Se crea entonces un nuevo marco de pila para **g()**, en el que se almacenan copias de los valores contenidos en los registros no volátiles que vayan a utilizarse, los argumentos y las variables locales que no puedan contenerse directamente en los registros y/o los argumentos para las funciones invocadas posteriormente por g(). Al finalizar la ejecución de g(), el puntero de pila será incrementado hasta su valor original.

![[Pasted image 20251015152611.png]]

Con carácter general, el puntero de pila permanece estático a lo largo del cuerpo de una función, lo que permite su uso como referencia para recorrer la pila en combinación con metadatos generados por el compilador y almacenados en el ejecutable. En contraste con la arquitectura x86, pues, no es necesaria la utilización del puntero de marco %ebp (frame pointer) para referenciar el fondo de la pila.

El modo de funcionamiento de la pila, que sigue el esquema LIFO (Last In, First Out), es simple y determinista. Es más, la reutilización constante de la pila tiende a mantenerla activa en la memoria caché de la CPU, por lo que el acceso a sus datos resulta enormemente eficiente.

## 4.1. Errores comunes en la gestión de la pila de usuario
Cabe citar dos errores de software (bugs) comunes en la gestión de la pila, a saber:

- Si el uso de memoria sobrepasase el tamaño máximo permitido para la pila (fenómeno conocido como stack overflow) se produciría una violación de acceso (segmentation fault), lo que conduciría a una interrupción inesperada del proceso (crash del proceso). Esto puede ocurrir, por ejemplo, si se agotan los recursos de la pila al almacenar en ella variables de gran tamaño (en cuyo caso debiera procederse a alojarlas en la memoria dinámica, cuyo tamaño máximo es del orden del GiB), o bien durante la ejecución de algoritmos recursivos infinitos o demasiado profundos.
- Sobrescribir un array más allá de su cota superior puede corromper la pila, siendo este un error difícil de detectar en un proceso de depuración (debugging). Esta última situación es fácilmente evitable de hacerse uso de las técnicas de programación propias del lenguaje C++. Así, por ejemplo, de ser asumible el coste de utilizar excepciones en nuestro sistema, puede emplearse la plantilla de clase std::array<> y su función miembro at() con control de acceso en sustitución de los arrays estáticos tradicionales del lenguaje C.
![[Pasted image 20251015164802.png]]


# 5. Punteros – Definiciones básicas
Al trabajar con el lenguaje C++ resulta inevitable el empleo de punteros, ya sea para referenciar objetos *alojados*[^5] en la *memoria libre*[^6], para introducir polimorfismo dinámico en nuestro código o con el fin de operar con estructuras dinámicas de datos y sus iteradores. Familiarizarse con este tipo de variables, hasta el punto de convertir su manipulación en una tarea natural para el programador, requiere esfuerzo y numerosas horas de práctica. Sin embargo, los conceptos básicos involucrados en su aprendizaje resultan extremadamente simples.

![[Pasted image 20251020151320.png]]

Un puntero es una variable que almacena la dirección en memoria de otro objeto. Como tal, el espacio ocupado en memoria por un puntero (independientemente del tipo de objeto que referencie) coincide con el número de bytes necesario para especificar una dirección de memoria (**4 bytes** en la arquitectura **x86**, **8 bytes** en la arquitectura **x86-64**). La sintaxis básica para definir un puntero (en el ejemplo, referenciando a un entero) es:

```cpp
int n = 0; // n es un entero 
int* p = &n; // p es un puntero que apunta al entero n
```

Aquí, p es un puntero de tipo int* (es decir, un puntero a entero), que referencia al entero n. Observemos, en particular, el empleo del operador unitario dirección-de (**&**) para **obtener la dirección de n** e iniciar con ella p. Así pues, p almacena la dirección en memoria de n.
La siguiente operación de inserción:
```cpp
std::cout << p; // imprime la dirección de n
```
![[Pasted image 20251020152610.png]]

imprime en la salida estándar la dirección en memoria de n en formato hexadecimal. Para poder acceder al objeto referenciado por el puntero, debemos utilizar el operador unitario de indirección o **desreferencia** (* ).

```cpp
std::cout << *p; // imprime 0
```

imprime el valor numérico de n (en este caso, cero). 
De igual forma, la operación:
```cpp
*p = 1; // el valor de n es ahora 1
```

redefine el valor del objeto apuntado (el entero n) como la unidad.
De imprimirse ahora el entero, obtendremos dicho nuevo valor en la salida estándar:
```cpp
std::cout << n; // imprime 1
```

Si deseamos referenciar una variable a través de un puntero con el fin de realizar operaciones de lectura, pero **no de escritura**, debemos definir un puntero a objeto constante según la sintaxis siguiente:
```cpp
int const* q = &n; // podemos leer el valor de n a través de q, pero no modificarlo
```


## Conclusión 
```cpp
#include <iostream>

using namespace std;

  

int main()

{

   int n = 0;

   int *p = &n;

  

   cout << "Dirección de n (puntero p): " << p << endl; // 0x5ffe6c

   cout << "Valor apuntado por p: " << *p << endl;      // 0

  

   *p = 1;

   cout << "Nuevo valor de n después de *p = 1: " << n << endl; // 1

  

   int const *q = &n;

   cout << "Valor apuntado por puntero constante q: " << *q << endl; // 1

  

   *p = 999;

   cout << "Dirección de n (puntero p): " << p << endl; // 0x5ffe6c

   cout << "Dirección de n (puntero q): " << q << endl; // 0x5ffe6c

  

   return 0;

}
```


# 6. Punteros - Estructuras de datos
Consideremos ahora un puntero que apunte a un **objeto** de una estructura o clase, como en el siguiente ejemplo:
```cpp
#include <iostream>

using namespace std;

  

struct Student

{

    string name;

    double grade_1, grade_2, grade_3;

    double average() const

    {

        return (grade_1 + grade_2 + grade_3) / 3.0;

    }

};

  

int main()

{

    Student s{"Nico Ni", 5.0, 5.0, 5.0};

    Student *p = &s;

    cout << "El valor de puntero p: " << p << endl;

    cout << "La nota media de grade_1,grade_2, grade_3: " << (*p).average() << endl;

    ;

    (*p).grade_1 = 10.0;

    cout << "La nota media con grade_1 10.0: " << (*p).average() << endl;

    return 0;

}
```

La sintaxis anterior puede simplificarse, sin embargo, mediante el empleo del operador flecha de acceso **(->)** en la forma siguiente (compárense ambos códigos):

```cpp
p->grade_2 = 8.5;
std::cout << p->average(); // imprime 8.0
```

# 7. Puntero nulo
Un puntero **sin asignar** no referencia a un objeto válido, de manera que su desreferencia dará lugar a un comportamiento indefinido del proceso (undefined behavior):
```cpp
#include <iostream>
using namespace std;

int main(){
    int *p;
    cout<< *p;
}

```
![[Pasted image 20251020155637.png]]


Podemos indicar explícitamente que un puntero no apunta a un objeto válido mediante la palabra clave **nullptr** (puntero nulo):
![[Pasted image 20251020155731.png]]

### 🔹 Usos principales de `nullptr`

1. **Inicializar punteros vacíos**
    
```cpp
int* p = nullptr; // p no apunta a nada aún
```
    
2. **Indicar que un puntero no tiene objeto asignado**
    
    - Sirve como señal: “aquí no hay nada” o “la operación falló”.
```cpp
Nodo* buscar(int valor) {
    if (valor no está en la lista)
        return nullptr; // no se encontró
    else
        return puntero_al_nodo;
}

```
3. **Condiciones y comprobaciones de punteros**
    
```cpp
if (p == nullptr) {
    cout << "El puntero no apunta a nada";
}
```
    
4. **Evitar errores de punteros sin inicializar**
    
    - Un puntero no inicializado puede apuntar a cualquier lugar y causar errores.
    - Inicializarlo con `nullptr` evita estos problemas.
5. **Diferencia clara con enteros**
    - `nullptr` es un tipo especial (`std::nullptr_t`) que **solo puede usarse como puntero**, evitando confusiones que ocurrían con `0` o `NULL`.


# 8. Memoria libre(free store)
El lenguaje C++ permite la **asignación dinámica de memoria** en el sector de memoria libre (free store) mediante expresiones de tipo **new**. *En contraste con*[^7] el alojamiento de variables locales en la **pila del usuario**, el tiempo de vida de los objetos alojados dinámicamente no se encuentra limitado al ámbito en que fueron creados, de forma que la memoria debe ser reclamada explícitamente a través de una expresión **delete** o, muy raramente, mediante un recolector de basura.

 **el tiempo de vida de los objetos alojados dinámicamente**
🔹 Variables locales (pila / stack)
```cpp
void f() {
    int x = 10; // x existe aquí
} // x desaparece al terminar f()

```
- **Tiempo de vida**: desde que se declara `x` hasta que termina la función `f()`.
- Una vez que la función termina, la memoria de `x` se libera automáticamente.

🔹 Objetos dinámicos (heap / memoria libre)
```cpp
int* p = new int(5); // objeto creado en memoria dinámica
// todavía existe aunque la función termine
delete p; // ahora la memoria se libera
```
- **Tiempo de vida**: desde que usas `new` hasta que llamas a `delete`.
- No importa si la función donde se creó termina: el objeto **sigue existiendo en memoria**.
- Esto permite que otras partes del programa sigan usando ese objeto mientras no lo elimines.

**expresión:** 
![[Pasted image 20251020160612.png]]

**Algunos no entiendo...**
![[Pasted image 20251020161117.png]]

![[Pasted image 20251020161245.png]]

# 9. Expresiones delete
Observemos que la variable de retorno en una expresión **new** es un puntero al tipo de **objeto** construido, proporcionando su dirección en memoria. Dicho puntero es una **variable local** y, como tal, su duración de almacenamiento finaliza *al concluir*[^8] el ámbito en que fue definida. No ocurre así con el **objeto referenciado** , que seguirá almacenado en memoria libre **hasta ser destruido** (y su espacio en memoria desalojado) explícitamente mediante una expresión **delete**. Esta propiedad puede dar lugar a fugas de memoria (memory leaks) indeseadas:

![[Pasted image 20251020162552.png]]

```cpp
{ X* p = new X; } 
// el puntero sale fuera de ámbito en este punto,
// pero el objeto al que apunta sigue almacenado en la memoria libre (memory leak)
```

Estas fugas deberán ser evitadas mediante técnicas modernas de programación (en particular, haciendo uso de los **punteros inteligentes** proporcionados por el estándar del lenguaje).

Una expresión de tipo **delete** como la siguiente:
```cpp
X* p = new X;
// ... 
delete p;
```

**Es equivalente a:**
```cpp
X* p = new X; 
// ... 
if (p != nullptr) {
	p->~X(); // llamamos al destructor de X 
	operator delete(p); // y desalojamos la memoria reservada para el objeto
}
```
Es decir, si el puntero es no nulo, se invoca al **destructor** de la clase para el objeto referenciado y **se libera la memoria ocupada por este**. Hacemos notar aquí la importancia de que el destructor de la clase X *no emita excepciones de ningún tipo*[^9]. En el ejemplo considerado, la razón es evidente: de lanzarse una excepción desde el destructor, el operador delete (**responsable de la liberación del bloque de memoria ocupado por el objeto**) no será invocado, produciéndose una laguna de memoria.
![[Pasted image 20251020164424.png]]

# 10. Fragmentación de la memoria libre
En la mayoría de los compiladores del lenguaje C++, el **alojamiento dinámico** en el espacio de memoria libre (mediante expresiones new y delete) suele venir implementado *en torno a*[^10] las funciones malloc() y free() propias del lenguaje C. Por cada nueva petición de almacenamiento, el sistema debe realizar una búsqueda efectiva de un bloque en memoria sin utilizar de un tamaño igual o superior al solicitado. De no existir suficiente espacio en memoria, se notifica el error emitiendo por defecto una excepción de tipo **std::bad_alloc**. Existen múltiples algoritmos de alojamiento posibles, cada uno de los cuales posee sus ventajas y sus inconvenientes en relación a su eficiencia en la búsqueda y uso de la memoria.

Con el fin de entender el modo en que la memoria libre se fragmenta tras un uso continuado de expresiones **new/delete** y los problemas que esto conlleva, consideremos un segmento de memoria virtual de tan sólo 12 KiB de tamaño.

- En un inicio, el bloque de memoria se encuentra inutilizado.
	 ![[Pasted image 20251020165258.png]]
- Supongamos que se realizan secuencialmente las operaciones de alojamiento de tres objetos de **4 KiB** de tamaño, respectivamente.
	![[Pasted image 20251020165333.png]]

- Posteriormente, el desalojo del **primer** y **último** objeto, tal y como muestra la siguiente imagen. La memoria libre ha quedado, como se ve, **fragmentada**[^11]. De hecho, de requerirse a continuación un alojamiento **adicional de 8 KiB** de memoria, se produciría la emisión de una excepción informando de la imposibilidad de dicha operación. En efecto, aun cuando existan un total de 8 KiB libres en memoria, no es posible alojarlos de manera contigua.
	![[Pasted image 20251020165405.png]]

Un buen diseño de la aplicación resulta crítico a la hora de mitigar la fragmentación de la memoria virtual. Así, por ejemplo, si una operación que vaya a repetirse múltiples veces en tiempo de ejecución requiriese la creación de un **búfer** de tamaño conocido, es claro que el desarrollador debería alojar la memoria una única vez, procediendo a su reutilización cuantas veces sea necesario, y no permitir que el búfer sea alojado y desalojado cada vez que este tenga que ser utilizado.


**Que significa "dinámico"**
![[Pasted image 20251020164700.png]]

|Tipo|Ejemplo|Decide tiempo de vida|Gestión|
|---|---|---|---|
|Automática|`int n;`|Entrada/salida de función|Compilador|
|Dinámica|`int* p = new int;`|Tiempo de ejecución|Programador (`delete`)|

# 11. Aritmética de punteros
Como hemos explicado anteriormente en este tema, una expresión de tipo new devuelve un puntero al objeto recién alojado en la memoria libre (free store):
```cpp
Student* p = new Student{"Sarah Cole",6.0,8.5,9.5};
```

Aquí, el **puntero p** alojado en la **pila** apunta a un **objeto de tipo Student** alojado en **memoria libre**. El acceso a los datos y funciones miembro públicas de dicha estructura puede realizarse, como ya hemos explicado, a través del operador **->**.

Como sabemos, es también posible crear una **matriz unidimensional** de objetos de tipo Student, ubicados en bloques consecutivos de memoria libre, a través de una expresión **new[]**:

```cpp
Student* p = new Student[100]; // matriz unidimensional de 100 estudiantes
```

Cada uno de los objetos en la matriz anterior es inicializado por defecto mediante una llamada al constructor por defecto de la estructura Student. El puntero p referencia al primer objeto de la matriz, mientras que el puntero **(p + i)**, donde i es un entero positivo o nulo, referencia al elemento i-ésimo de la matriz (Imagen VI).
![[Pasted image 20251020170533.png]]

Así, si deseamos modificar la primera calificación obtenida por el quinto alumno en nuestra lista (es decir, el correspondiente al índice de acceso 4), podemos emplear una cualquiera de las siguientes expresiones:
![[Pasted image 20251020170549.png]]
Ahora bien, atendiendo al hecho de que la dirección almacenada en el puntero p coincide con la dirección de inicio de la matriz, podemos también **utilizar la indexación habitual** de C y C++ para matrices y escribir:

![[Pasted image 20251020170559.png]]

Esta última sintaxis es la más conveniente por razones evidentes.

![[Pasted image 20251020171709.png]]


![[Pasted image 20251020171732.png]]

[^2]: #### **Pila del usuario (user stack)**
	
	- La **pila** es una **zona de memoria especial** que usan los programas para guardar información temporal:
	    
	    - Variables locales de funciones
	        
	    - Direcciones de retorno (a dónde volver después de llamar a una función)
	        
	    - Parámetros de funciones
	        
	- Funciona como una **pila de platos**: lo último que se mete es lo primero que se saca (**LIFO: Last In, First Out**).

[^3]: #### **Alojar memoria en la pila**
	
	- Para **meter algo nuevo** en la pila, el procesador **disminuye %rsp**.
	    
	    - Esto “reserva” espacio en la pila porque la pila crece hacia direcciones de memoria más bajas.
	        
	
	#### 4. **Desalojar memoria de la pila**
	
	- Para **sacar algo de la pila**, el procesador **aumenta %rsp**.
	    
	    - Esto “libera” espacio, dejando la cima de la pila en la posición anterior.

[^4]: La **pila del usuario** es un espacio de memoria temporal que crece hacia abajo.  
	El registro **%rsp** apunta siempre a la cima de la pila.
	
	- Para **meter cosas** (alojar memoria) → **disminuyes %rsp**
	    
	- Para **sacar cosas** (desalojar memoria) → **aumentas %rsp**

[^5]: La palabra **“alojado”** viene del verbo **“alojar”**, que significa **dar un lugar donde quedarse o guardarse**.  
	En español común, decimos por ejemplo:
	
	- “El hotel **aloja** a los turistas.” → el hotel **les da un lugar** donde dormir.
	    
	- “Los archivos están **alojados** en el servidor.” → los archivos **están guardados** allí.
	    
	
	💡 En informática o programación, **“alojar algo en memoria”** quiere decir:  
	👉 **guardar algo (como un objeto o dato) dentro de una parte específica de la memoria del ordenador.**

[^6]: memoria libre refiere  **heap** (montículo),

[^7]: **“a diferencia de”** o **“comparado con algo que es distinto”**.

[^8]: La expresión **“al concluir”** significa simplemente:
	> **“cuando termina”** o **“cuando se llega al final de”**.
	
	### 🔹 Ejemplo:
	```cpp
	void f() {
	    int x = 10; // x se crea aquí
	} // <-- aquí concluye el ámbito
	// x ya no existe
	```
	- El **ámbito** es la función `f()`.
	- **“Al concluir”** la función, la variable `x` desaparece de la memoria.
	

[^9]: Esto significa:
	
	- El destructor **no debe lanzar ninguna excepción**.
	    
	- En C++ se dice que el destructor debe ser **`noexcept`** por defecto.
	    
	
	Ejemplo malo:
	```cpp
	class X {
	public:
	    ~X() {
	        throw std::runtime_error("Error en destructor"); // ❌ peligroso
	    }
	};
	
	```
	
	Si hacemos `delete p` en este caso:
	
	1. Se llama al destructor `~X()`.
	    
	2. El destructor lanza una excepción.
	    
	3. **El operador `delete` nunca llega a liberar la memoria** porque la excepción interrumpe el flujo.
	    
	4. Resultado: **memory leak** (fuga de memoria).

[^10]: “en torno a X”, piensa **“alrededor de X” o “sobre X”**.

[^11]: “Fragmentada” significa que **la memoria libre no está en un solo bloque contiguo**, sino en pedazos dispersos.
	
	Ejemplo: `[libre][B][libre]`.
