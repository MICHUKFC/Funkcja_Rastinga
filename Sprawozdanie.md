# Algorytm Genetyczny do Optymalizacji Wielowymiarowej Funkcji Rastrigina

Algorytm genetyczny to heurystyczna metoda optymalizacji, która naśladuje procesy ewolucyjne w przyrodzie, takie jak selekcja, krzyżowanie i mutacja, w celu znalezienia najlepszego rozwiązania problemu.

## Funkcje i ich opis

### 1. Rastrigin
Funkcja Rastrigina jest często używaną funkcją testową w optymalizacji. Jej wzór to:

`f(x) = A * n + sum(i=1 to n) [ x_i^2 - A * cos(2*pi * x_i) ]`

Gdzie:
- \( n \) - liczba wymiarów funkcji
- \( A \) - stała, zazwyczaj równa 10

### 2. Decode
Funkcja dekodująca konwertuje binarną reprezentację genomu na wartości rzeczywiste, które są używane jako argumenty funkcji Rastrigina.

### 3. Fitness
Funkcja oceny, która oblicza wartość funkcji Rastrigina dla danego genomu. W tym przypadku chcemy minimalizować wartość funkcji Rastrigina, więc negujemy jej wartość.
Natomiast jeżeli chcemy szukać maksimum funkcji należy usunąć minus z linii `return -rastrigin(x);` w linii `39`.

### 4. Mutate
Funkcja mutacji wprowadza niewielkie zmiany w genomie z pewnym prawdopodobieństwem. Mutacja jest kluczowym elementem algorytmów genetycznych, który zapobiega stagnacji populacji.

### 5. Crossover
Krzyżowanie (lub reprodukcja) polega na tworzeniu nowych genotypów poprzez łączenie części dwóch genotypów rodzicielskich.

### 6. Selekcje: Tournament, Roulette, Rank
Są to różne metody wyboru osobników do krzyżowania:
- **Turniej**: Wybiera kilka losowych osobników i zwraca najlepszego z nich.
- **Ruletka**: Wybiera osobników proporcjonalnie do ich przystosowania.
- **Ranking**: Osobniki są sortowane według ich przystosowania i wybierane na podstawie przypisanej im rangi.

## Zasada działania
Algorytm zaczyna od losowo wygenerowanej populacji osobników. W każdym pokoleniu osobniki są oceniane za pomocą funkcji przystosowania, a następnie wybierane są do reprodukcji na podstawie jednej z metod selekcji. Nowe pokolenie jest tworzone poprzez krzyżowanie wybranych osobników i potencjalne wprowadzenie mutacji.

Algorytm kontynuuje ten proces przez określoną liczbę pokoleń lub do momentu, gdy zostaną spełnione inne kryteria zakończenia.

