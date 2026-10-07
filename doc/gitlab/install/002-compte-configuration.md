# Configuration SSH GitLab avec plusieurs comptes

## 1. Générer la clé SSH en local

Sur ton poste Windows, dans PowerShell ou Git Bash :

```bash
ssh-keygen -t ed25519 -C "user01@gmail.com"
```

La clé est générée localement dans :

```text
C:\Users\chend\.ssh
```

Exemple de fichiers :

```text
C:\Users\chend\.ssh\user01
C:\Users\chend\.ssh\user01.pub
```

Le fichier sans extension est la clé privée.

Le fichier :

```text
user01.pub
```

est la clé publique.

---

## 2. Ajouter la clé publique sur GitLab

Sur le site GitLab :

```text
Preferences
→ SSH Keys
→ Add new key
```

Copier le contenu du fichier :

```text
C:\Users\chend\.ssh\user01.pub
```

et le coller dans GitLab.

---

## 3. Configurer le fichier SSH local

Le fichier de configuration SSH se trouve dans :

```text
C:\Users\chend\.ssh\config
```

Le fichier s'appelle exactement :

```text
config
```

sans extension.

Ajouter dans ce fichier :

```text
Host gitlab-user01
  HostName gitlab.com
  User git
  IdentityFile ~/.ssh/user01
  IdentitiesOnly yes
```

Cette configuration signifie :

```text
gitlab-user01
→ connexion vers gitlab.com
→ utilisateur SSH git
→ utilisation de la clé ~/.ssh/user01
```

---

## 4. Tester la connexion

En local :

```bash
ssh -T git@gitlab-user01
```

Si la configuration est correcte, GitLab reconnaît le compte associé à la clé SSH.

---

## 5. Utiliser cet alias pour Git

Au lieu d'utiliser :

```text
git@gitlab.com:user01/projet.git
```

utiliser :

```text
git@gitlab-user01:user01/projet.git
```

Exemple :

```bash
git clone git@gitlab-user01:user01/projet.git
```

L'alias :

```text
gitlab-user01
```

permet à SSH de sélectionner automatiquement la clé :

```text
~/.ssh/user01
```