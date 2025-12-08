![[Pasted image 20251022165034.png]]

![[Pasted image 20251023095854.png]]
# 3. destructores
![[Pasted image 20251022170529.png]]

Variable  `X b{2};` y  ` X a{1};` como stack. FILO
![[Pasted image 20251022170724.png]]
los objetos a y b son destruidos en **orden inverso** a su creación invocándose automáticamente al destructor de su clase.

![[Pasted image 20251022171323.png]]


## Conocimiento extra
```cpp
class NombreClase {
private:   // → Miembros privados (solo accesibles dentro de la clase)
    TipoDato miembro1;
    TipoDato miembro2;

public:   // → Miembros públicos (accesibles desde fuera)
    // Constructor(es)
    NombreClase(TipoDato param1, TipoDato param2) : miembro1{param1}, miembro2{param2} {
        // Código del constructor (opcional)
    }

    // Métodos públicos
    void metodo1();
    TipoDato metodo2() const;

    // Destructor (opcional)
    ~NombreClase() {
        // Código que se ejecuta al destruir el objeto
    }
};
```
# 4. Excepciones
**¿Qué ocurre con los objetos locales cuando se lanza una excepción?**

Al emitirse una excepción dentro de un bloque **try-catch**, el control del flujo se transfiere desde el punto de lanzamiento de la misma hasta la primera cláusula **catch** que pueda manejarla. Al alcanzarse dicha cláusula, todos los objetos con almacenamiento automático que hayan sido creados desde el inicio del bloque **try** son destruidos en orden inverso a su creación (invocándose a los destructores de sus clases de forma automática), en un proceso de **desenredo de la pila**.

Es decir, 

## En palabras sencillas
### 🧩 1. Contexto general

Cuando ocurre una **excepción** (`throw ...`), el programa **interrumpe la ejecución normal** y **salta** al primer `catch` que pueda manejarla.

Durante ese salto, el sistema tiene que “**desenredar la pila**” (en inglés _stack unwinding_), es decir:  
eliminar correctamente todos los objetos locales que se habían creado, para evitar fugas de memoria o recursos abiertos.

---

### ⚙️ 2. “Objetos con almacenamiento automático”

Significa los **objetos locales** (como los que declaras dentro de una función o bloque),  
por ejemplo:
```cpp
void f() {
    X x1; // almacenamiento automático
}
```

Cuando sales del bloque, **se destruyen automáticamente** (llamando a su destructor).  
Esto también pasa si sales **porque hubo una excepción**.

---

### 🔁 3. “Orden inverso a su creación”

Si dentro del bloque `try` creaste varios objetos,  
al lanzarse una excepción **se destruyen en orden inverso**:

- El último que se creó, es el primero que se destruye.  
    Esto sigue la lógica de una pila (_stack_): **LIFO** (_Last In, First Out_).