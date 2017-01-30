print("Digite um numero: ");
n = read();
n = n+0;

i = 1;
fat = 1;
while i < n do
        i = i + 1;
        fat = fat *  i;
end;
print("Fatorial de ", n, "eh", fat);
