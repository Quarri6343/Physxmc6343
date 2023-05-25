package com.quarri6343.physxmc6343;

import physx.PxTopLevelFunctions;
import physx.common.*;
import physx.geometry.PxBoxGeometry;
import physx.physics.*;

import java.util.ArrayList;
import java.util.List;

public class Physx {

    //PhysX library version
    private static final int version = PxTopLevelFunctions.getPHYSICS_VERSION();

    //PhysX foundation object
    private static PxDefaultAllocator allocator;
    private static PxDefaultErrorCallback errorCb;
    private static PxFoundation foundation;

    //PhysX main physics object
    private static PxTolerancesScale tolerances;
    private static PxPhysics physics;

    //the CPU dispatcher, can be shared among multiple scenes
    private static PxDefaultCpuDispatcher cpuDispatcher;

    //create default material
    private static PxMaterial defaultMaterial;
    private PxScene scene;
    private PhysxGround ground;
    
    static {
        allocator = new PxDefaultAllocator();
        errorCb = new PxDefaultErrorCallback();
        foundation = PxTopLevelFunctions.CreateFoundation(version, allocator, errorCb);
        tolerances = new PxTolerancesScale();
        physics = PxTopLevelFunctions.CreatePhysics(version, foundation, tolerances);
        cpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(4);
        defaultMaterial = physics.createMaterial(0.5f, 0.5f, 0.5f);
    }
    
    public void setUpScene(){
        scene = createScene();
        
        ground = new PhysxGround(physics);
        scene.addActor(ground.createGround(defaultMaterial));
    }
    
    public void destroyScene(){
        if(scene != null){
            if(ground != null){
                scene.removeActor(ground.getActor());
                ground.release();
            }

            scene.release();   
        }
    }
    
    public PhysxBox addBox(PxVec3 pos, PxQuat quat){
        PhysxBox box = new PhysxBox(physics);
        scene.addActor(box.createBox(defaultMaterial, pos, quat));
        return box;
    }

    public PhysxBox addBox(PxVec3 pos, PxQuat quat, PxBoxGeometry boxGeometry){
        PhysxBox box = new PhysxBox(physics);
        scene.addActor(box.createBox(defaultMaterial, pos, quat, boxGeometry));
        return box;
    }
    
    public void removeBox(PhysxBox box){
        scene.removeActor(box.getActor());
        box.release();
    }
    
    public void tick(){
        scene.simulate(1f/60f);
        scene.fetchResults(true);
    }
    
    public PxScene createScene(){
        // create a physics scene
        PxVec3 tmpVec = new PxVec3(0f, -19.62f, 0f);
        PxSceneDesc sceneDesc = new PxSceneDesc(tolerances);
        sceneDesc.setGravity(tmpVec);
        sceneDesc.setCpuDispatcher(cpuDispatcher);
        sceneDesc.setFilterShader(PxTopLevelFunctions.DefaultFilterShader());
        PxScene scene = physics.createScene(sceneDesc);

        tmpVec.destroy();
        sceneDesc.destroy();
        
        return scene;
    }
    
    public void terminate(){
        if(defaultMaterial != null){
            defaultMaterial.release();
            tolerances.destroy();

            physics.release();

            foundation.release();
            errorCb.destroy();
            allocator.destroy(); 
        }
    }
}
