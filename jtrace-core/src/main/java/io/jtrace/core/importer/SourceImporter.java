package io.jtrace.core.importer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithModifiers;
import com.github.javaparser.ast.type.Type;
import io.jtrace.core.importer.ProjectModel.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Imports and parses Java source code files.
 */
public class SourceImporter {
    private final JavaParser javaParser;
    
    public SourceImporter() {
        this.javaParser = new JavaParser();
    }
    
    public ProjectModel importSources(List<Path> sourcePaths) {
        ProjectModel model = new ProjectModel();
        
        for (Path sourcePath : sourcePaths) {
            try {
                String content = Files.readString(sourcePath);
                ParseResult<CompilationUnit> result = javaParser.parse(content);
                
                if (result.isSuccessful() && result.getResult().isPresent()) {
                    CompilationUnit cu = result.getResult().get();
                    processCompilationUnit(cu, sourcePath, model);
                }
            } catch (Exception e) {
                System.err.println("Warning: Could not parse " + sourcePath + ": " + e.getMessage());
            }
        }
        
        return model;
    }
    
    private void processCompilationUnit(CompilationUnit cu, Path sourcePath, ProjectModel model) {
        Optional<PackageDeclaration> packageDecl = cu.getPackageDeclaration();
        String packageName = packageDecl.map(p -> p.getNameAsString()).orElse("");
        
        PackageInfo packageInfo = model.getPackage(packageName);
        if (packageInfo == null) {
            packageInfo = new PackageInfo(packageName);
            model.addPackage(packageInfo);
        }
        
        // Process imports
        Set<String> imports = cu.getImports().stream()
            .map(ImportDeclaration::getNameAsString)
            .collect(Collectors.toSet());
        packageInfo.addImports(imports);
        
        // Process classes
        for (TypeDeclaration<?> typeDecl : cu.getTypes()) {
            ClassInfo classInfo = processTypeDeclaration(typeDecl, packageName, sourcePath);
            packageInfo.addClass(classInfo);
            model.addClass(classInfo);
            
            // Process inner classes
            for (TypeDeclaration<?> innerType : typeDecl.findAll(TypeDeclaration.class)) {
                if (innerType != typeDecl) {
                    ClassInfo innerClassInfo = processTypeDeclaration(innerType, packageName, sourcePath);
                    innerClassInfo.setEnclosingClass(classInfo.getName());
                    packageInfo.addClass(innerClassInfo);
                    model.addClass(innerClassInfo);
                }
            }
        }
    }
    
    private ClassInfo processTypeDeclaration(TypeDeclaration<?> typeDecl, String packageName, Path sourcePath) {
        String className = typeDecl.getNameAsString();
        String fullName = packageName.isEmpty() ? className : packageName + "." + className;
        
        Visibility visibility = determineVisibility(typeDecl);
        ClassType classType = determineClassType(typeDecl);
        
        ClassInfo classInfo = new ClassInfo(className, fullName, visibility, classType);
        classInfo.setSourceFile(sourcePath.toString());
        
        // Process fields
        for (FieldDeclaration field : typeDecl.findAll(FieldDeclaration.class)) {
            for (VariableDeclarator var : field.getVariables()) {
                FieldInfo fieldInfo = new FieldInfo(
                    var.getNameAsString(),
                    field.getCommonType().toString(),
                    determineVisibility(field),
                    field.getAnnotations().stream()
                        .map(AnnotationExpr::getNameAsString)
                        .collect(Collectors.toList())
                );
                classInfo.addField(fieldInfo);
            }
        }
        
        // Process methods
        for (MethodDeclaration method : typeDecl.findAll(MethodDeclaration.class)) {
            MethodInfo methodInfo = new MethodInfo(
                method.getNameAsString(),
                method.getType().toString(),
                determineVisibility(method),
                method.getParameters().stream()
                    .map(p -> new ParameterInfo(p.getNameAsString(), p.getType().toString()))
                    .collect(Collectors.toList()),
                method.getAnnotations().stream()
                    .map(AnnotationExpr::getNameAsString)
                    .collect(Collectors.toList())
            );
            classInfo.addMethod(methodInfo);
        }
        
        // Process annotations
        List<String> annotations = typeDecl.getAnnotations().stream()
            .map(AnnotationExpr::getNameAsString)
            .collect(Collectors.toList());
        classInfo.setAnnotations(annotations);
        
        return classInfo;
    }
    
    private Visibility determineVisibility(NodeWithModifiers<?> node) {
        if (node.isPublic()) return Visibility.PUBLIC;
        if (node.isProtected()) return Visibility.PROTECTED;
        if (node.isPrivate()) return Visibility.PRIVATE;
        return Visibility.PACKAGE_PRIVATE;
    }
    
    private ClassType determineClassType(TypeDeclaration<?> typeDecl) {
        if (typeDecl instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration coid = (ClassOrInterfaceDeclaration) typeDecl;
            if (coid.isInterface()) return ClassType.INTERFACE;
            return ClassType.CLASS;
        } else if (typeDecl instanceof EnumDeclaration) {
            return ClassType.ENUM;
        } else if (typeDecl instanceof AnnotationDeclaration) {
            return ClassType.ANNOTATION;
        }
        return ClassType.CLASS;
    }
}
