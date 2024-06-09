# Rapport de projet
## Introduction
Ce projet a été réalisé dans le cadre du cours de Pratiques de développement. Cette application permet de gérer des allocations. 

## Librairies utilisées
- **DBUnit** : pour les tests de bases de données.
- **JUnit** : pour les tests unitaires et d'intégration.
- **SLF4J** : pour la gestion des logs.

## Nouvelles fonctionnalités
- **Ajouter des tests sur la classe AllocationService** : ajout de tests unitaires pour la classes metionnée.
- **Refacooring de la classe AllocationService** : refactoring de la classe AllocationService afin d'utiliser la classe ParentAllocationParameters.
- **Modification de la méthode getParentDroitAllocation** : modification de la méthode en utilisant le principe de TDD afin de respecter un schéma donné.
- **Suppression d'un allocataire** : ajout de la fonctionnalité de suppression d'un allocataire (s'il ne possède pas de versements).
- **Modification d'un allocataire** : ajout de la fonctionnalité de modification d'un allocataire (uniquement le nom et prénom).
- **Exposer ces services et les tester** : exposer les services du point précédent en tant qu'API REST et les tester avec un outil de notre choix.
- **Ajout de la gestion des logs** : ajout d'une gestion des logs afin de remplacer les simple affichages dans la console.
- **Ajout de tests d'intégration** : ajout de tests d'intégration pour la suppression et la modification d'un allocataire.
