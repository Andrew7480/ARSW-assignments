package main

import (
	"fmt"
	"sync"
	"time"
)

func contador(inicio int64, fin int64, wg *sync.WaitGroup) {
	defer wg.Done()
	for i := inicio; i <= fin; i++ {
		fmt.Println(i)
	}
}

func main() {
	var limite int64 = 5000000
	var numHilos int64 = 1000
	rango := limite / numHilos
	var wg sync.WaitGroup
	inicio := time.Now()
	for i := int64(0); i < numHilos; i++ {
		start := i*rango + 1
		end := (i + 1) * rango
		if i == numHilos-1 {
			end = limite
		}
		wg.Add(1)
		go contador(start, end, &wg)
	}
	wg.Wait()
	fmt.Println("Tiempo:", time.Since(inicio).Milliseconds(), "ms")
}
