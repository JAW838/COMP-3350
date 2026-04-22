# Software Engineering Group – Code Standards & Practices

This document outlines the coding standards, structure guidelines, and workflow norms for our software engineering project. Following these guidelines ensures consistency, maintainability, and high-quality code throughout our team.

---

## 1. File & Project Structure

- **Naming Conventions**:  
  - Use  `PascalCase.file` for file names.
  - Folder names will be all `lowercase`
  - Avoid spaces and special characters.

- **Package Organization**:  
  - `presentation/` – User interface components  
  - `logic/` – Core application logic  
  - `objects/` – Data models and entities  
  - `persistence/` – Data access and storage mechanisms
  - `exceptions/` – Custom exceptions

- **Test Files**:  
  - Mirror the structure of the source code inside a `tests/` directory  
  - Test files should have the same name as the class or module being tested, with a suffix of `Test`

---

## 2. Naming Conventions

- **Classes**: `PascalCase` (e.g., `UserProfile`)  
- **Methods & Variables**: `camelCase` (e.g., `getUserName`, `userId`)  
- **Constants**: `ALL_CAPS_WITH_UNDERSCORES` (e.g., `MAX_RETRIES`)  
- **Naming Guidelines**:  
  - Be descriptive and clear  
  - Avoid unnecessary abbreviations (e.g., `calculateTotal()` over `calcTot()`)

---

## 3. Formatting & Style

- **Indentation**: Tabs  
- **Line Length**: Max 100 characters  
- **Braces**: Opening brace on the **same line** as the declaration  
- **Blank Lines**:  
  - Between method definitions  
  - Before `return` statements  
  - Around logical blocks within methods
```
for (int i = 0; i < array.length(); i++) {
  method(i, array.length());
}
```

---

## 4. Commenting Practices

- **Headers**:  
  - Every class and public method should include a brief comment  
- **Inline Comments**:  
  - Required where the logic is non-obvious
  - Comments go above, not on the same line
- **Tags**:  
  - Use `TODO:` for unfinished work  
  - Use `FIXME:` for known issues needing attention

---

## 5. Code Organization

- **Class Member Order**:  
  1. Constants  
  2. Fields (variables)  
  3. Constructors  
  4. Public methods  
  5. Private methods

- **Variables**:  
  - Group static variables separately from instance variables  
  - Avoid public fields; use getters/setters

---

## 6. Error Handling

- **try-catch Usage**:  
  - Use only where necessary (e.g., I/O operations, external services)  
  - Catch specific exceptions rather than generic ones  
- **Throwing Exceptions**:  
  - Use meaningful custom exceptions where applicable  
  - Document all thrown exceptions in method headers

---

## 7. Version Control Practices

- **Branch Naming**:  
  - `feature/<name>`, `bugfix/<issue>`, `hotfix/<patch>`  
- **Commit Messages**:  
  - Format: `brief description` (e.g., `add forgot password support`)  
  - Include issue/task number if relevant  
- **Workflow**:  
  - Push frequently; avoid large, single commits  
  - Use pull requests for all merges  
  - Peer review is required for merges to `main` or `dev` branches

---

## 8. Testing Standards

- **Coverage**:  
  - Aim for at least 80% unit test coverage  
- **Naming**:  
  - Test files: `ClassNameTest`  
  - Test methods: `shouldDoXWhenY()` format for clarity  
- **Structure**:  
  - Organize by component/module  
  - Group setup and teardown logic clearly  
- **Mocks**:  
  - Use mocks/fakes for dependencies to isolate tests

---

## 9. Dependency Management

- **Approved Libraries**:  
  - Only use libraries approved by the team  
  - Add all dependencies to the central configuration file (`build.gradle`)  

---

## 10. Collaboration & Workflow Norms

- **Pair Programming**:  
  - Encouraged for critical features or problem-solving  
- **Pull Requests**:  
  - Keep them concise and focused  
  - Include a short summary and link related tasks/issues  
  - Reviewers should leave constructive feedback  
- **Documentation**:  
  - Use a shared changelog to log major architectural or design decisions

---
