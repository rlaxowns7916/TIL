fun main(){
    val repo1 = Repo1()
    val repo2 = Repo2()

    val service = Service(repo1,repo2)
    println(service.invoke())
}

class Service(
    private val repo1: Repo1,
    private val repo2: Repo2
){
    fun invoke(continuation: Continuation?=null): Int {
        val state = continuation ?: object: Continuation{
            override var label: Int = 0
            override var data: Any? = null
            override fun resumeWith(result: Any) {
                data = result
                invoke(this)
            }
        }

        when(state.label){
            0 -> {
                state.label = 1
                repo1.invoke(state)
            }
            1 -> {
                state.label = 2
                repo2.invoke(state.data as Int, state)
            }
        }
        return (state.data as Int)
    }
}

class Repo1{
    fun invoke(continuation: Continuation){
        continuation.resumeWith(1)
    }
}

class Repo2{
    fun invoke(num: Int, continuation: Continuation){
        continuation.resumeWith(num + 2)
    }
}

interface Continuation{
    var label: Int
    var data: Any?
    fun resumeWith(result: Any)
}