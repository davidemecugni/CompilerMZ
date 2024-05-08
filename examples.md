
## Examples
Here are some examples of the .mz language:
### Default dialect
```manz
@@
Finds the nth prime(considering 2 the first prime number)
@@
{
    @Starts the count at 1
    let count = 1 ;
    let number = 2 ;
    let nThPrime = 0 ;
    read ( nThPrime ) ;
    while ( count < nThPrime + 1 ) {
        let is_prime = true ;
        let divisor = 2 ;
        @ Checks whether a number is prime
        while ( divisor * divisor <= number & is_prime ) {
            if ( number % divisor == 0 ) {
                is_prime = false ;
            }
            divisor = divisor + 1 ;
        }
        if ( is_prime ) {
            if ( count == nThPrime ) {
                @ Prints the nt prime
                print ( number ) ;
                exit ( 0 ) ;
            }
            count = count + 1 ;
        }
        number = number + 1 ;
    }
}
```
### Zanna dialect
```manz
comunquecomunque
Finds the nth prime(considering 2 the first prime number)
comunquecomunque
inizio
    comunqueStarts the count at 1
    giuro count è 1 e_basta
    giuro number è 2 e_basta
    giuro nThPrime è 0 e_basta
    leggi apro nThPrime chiudo e_basta
    finche apro count minore nThPrime più 1 chiudo inizio
        giuro is_prime è vero e_basta
        giuro divisor è 2 e_basta
        comunque Checks whether a number is prime
        finche apro divisor per divisor minoreè number anche is_prime chiudo inizio
            se apro number modulo divisor èè 0 chiudo inizio
                is_prime è falso e_basta
            finisco
            divisor è divisor più 1 e_basta
        finisco
        se apro is_prime chiudo inizio
            se apro count èè nThPrime chiudo inizio
                comunque Prints the nt prime
                scrivi apro number chiudo e_basta
                esco apro 0 chiudo e_basta
            finisco
            count è count più 1 e_basta
        finisco
        number è number più 1 e_basta
    finisco
finisco
```
### Emilian dialect
```manz
comèintcomèint
Finds the nth prime(considering 2 the first prime number)
comèintcomèint
{
    comèintStarts the count at 1
    métter count cumpàagn 1 ;
    métter number cumpàagn 2 ;
    métter nThPrime cumpàagn 0 ;
    léeṡer ( nThPrime ) ;
    fintàant ( count lé_più_cicch nThPrime più 1 ) {
        métter is_prime cumpàagn véra ;
        métter divisor cumpàagn 2 ;
        comèint Checks whether a number is prime
        fintàant ( divisor pèr divisor lé_più_cicchcumpàagn number aanch is_prime ) {
            sé_Dio_al_vóol ( number rèest divisor cumpàagncumpàagn 0 ) {
                is_prime cumpàagn fèls ;
            }
            divisor cumpàagn divisor più 1 ;
        }
        sé_Dio_al_vóol ( is_prime ) {
            sé_Dio_al_vóol ( count cumpàagncumpàagn nThPrime ) {
                comèint Prints the nt prime
                stampadóor ( number ) ;
                desmàtter ( 0 ) ;
            }
            count cumpàagn count più 1 ;
        }
        number cumpàagn number più 1 ;
    }
}
```
### Emoji dialect
```manz
🗨️🗨️
Finds the nth prime(considering 2 the first prime number)
🗨️🗨️
⬇️
    🗨️Starts the count at 1
    🔰 count ⏸ 1 📝
    🔰 number ⏸ 2 📝
    🔰 nThPrime ⏸ 0 📝
    📖 ➡️ nThPrime ⬅️ 📝
    🔄 ➡️ count ⏪ nThPrime ➕ 1 ⬅️ ⬇️
        🔰 is_prime ⏸ ✅ 📝
        🔰 divisor ⏸ 2 📝
        🗨️ Checks whether a number is prime
        🔄 ➡️ divisor ✖️ divisor ⏪⏸ number 🔛 is_prime ⬅️ ⬇️
            ❓ ➡️ number 🔢 divisor ⏸⏸ 0 ⬅️ ⬇️
                is_prime ⏸ ❌ 📝
            ⬆️
            divisor ⏸ divisor ➕ 1 📝
        ⬆️
        ❓ ➡️ is_prime ⬅️ ⬇️
            ❓ ➡️ count ⏸⏸ nThPrime ⬅️ ⬇️
                🗨️ Prints the nt prime
                🖨 ➡️ number ⬅️ 📝
                👿 ➡️ 0 ⬅️ 📝
            ⬆️
            count ⏸ count ➕ 1 📝
        ⬆️
        number ⏸ number ➕ 1 📝
    ⬆️
⬆️
```
### Minimal dialect
```manz
##
Finds the nth prime(considering 2 the first prime number)
##
{
    #Starts the count at 1
    $ count = 1 ;
    $ number = 2 ;
    $ nThPrime = 0 ;
    @ ( nThPrime ) ;
    § ( count < nThPrime + 1 ) {
        $ is_prime = ^ ;
        $ divisor = 2 ;
        # Checks whether a number is prime
        § ( divisor * divisor <= number & is_prime ) {
            ? ( number % divisor == 0 ) {
                is_prime = ç ;
            }
            divisor = divisor + 1 ;
        }
        ? ( is_prime ) {
            ? ( count == nThPrime ) {
                # Prints the nt prime
                £ ( number ) ;
                ° ( 0 ) ;
            }
            count = count + 1 ;
        }
        number = number + 1 ;
    }
}
```