import bpy
import math
from mathutils import Vector, Euler

def clear_scene():
    bpy.ops.wm.read_factory_settings(use_empty=True)
    for col in (bpy.data.objects, bpy.data.meshes, bpy.data.materials, bpy.data.lights, bpy.data.cameras):
        for item in col:
            col.remove(item)

def create_material(name, base_color, metallic=0.0, roughness=0.5, emission_color=(0,0,0,1), emission_strength=0.0):
    mat = bpy.data.materials.new(name=name)
    nodes = mat.node_tree.nodes
    bsdf = nodes.get("Principled BSDF")
    if bsdf:
        if "Base Color" in bsdf.inputs:
            bsdf.inputs["Base Color"].default_value = base_color
        if "Metallic" in bsdf.inputs:
            bsdf.inputs["Metallic"].default_value = metallic
        if "Roughness" in bsdf.inputs:
            bsdf.inputs["Roughness"].default_value = roughness
        if "Emission Color" in bsdf.inputs:
            bsdf.inputs["Emission Color"].default_value = emission_color
        if "Emission Strength" in bsdf.inputs:
            bsdf.inputs["Emission Strength"].default_value = emission_strength
        if "Specular IOR Level" in bsdf.inputs:
            bsdf.inputs["Specular IOR Level"].default_value = 0.5
    return mat

def create_primitive(prim_type, name, location=(0,0,0), scale=(1,1,1), rotation=(0,0,0), material=None, parent=None, bevel=0.0, **kwargs):
    if prim_type == 'cube':
        bpy.ops.mesh.primitive_cube_add(location=location, **kwargs)
    elif prim_type == 'cylinder':
        bpy.ops.mesh.primitive_cylinder_add(location=location, **kwargs)
    elif prim_type == 'uv_sphere':
        bpy.ops.mesh.primitive_uv_sphere_add(location=location, **kwargs)
    elif prim_type == 'cone':
        bpy.ops.mesh.primitive_cone_add(location=location, **kwargs)
    elif prim_type == 'torus':
        bpy.ops.mesh.primitive_torus_add(location=location, **kwargs)
    
    obj = bpy.context.active_object
    obj.name = name
    obj.scale = scale
    obj.rotation_euler = rotation
    
    if material:
        if obj.data.materials:
            obj.data.materials[0] = material
        else:
            obj.data.materials.append(material)
            
    if parent:
        obj.parent = parent
        
    for poly in obj.data.polygons:
        poly.use_smooth = True

    if bevel > 0.0:
        b_mod = obj.modifiers.new(name="Bevel", type='BEVEL')
        b_mod.width = bevel
        b_mod.segments = 2
        
    return obj

def build_freefire_character():
    clear_scene()

    # --- MATERIALS ---
    mat_skin = create_material("FF_Skin", (0.84, 0.65, 0.52, 1.0), roughness=0.55)
    mat_tactical_black = create_material("FF_TacticalBlack", (0.05, 0.05, 0.06, 1.0), metallic=0.15, roughness=0.38)
    mat_armor = create_material("FF_ArmorPlate", (0.13, 0.14, 0.16, 1.0), metallic=0.45, roughness=0.28)
    mat_ff_orange = create_material("FF_OrangeAccent", (1.0, 0.32, 0.01, 1.0), metallic=0.1, roughness=0.3)
    mat_cyber_cyan = create_material("FF_NeonCyan", (0.0, 0.9, 1.0, 1.0), roughness=0.1, emission_color=(0.0, 0.95, 1.0, 1.0), emission_strength=8.0)
    mat_pants = create_material("FF_CargoPants", (0.16, 0.19, 0.17, 1.0), roughness=0.7)
    mat_hair = create_material("FF_CyberHair", (0.95, 0.95, 0.97, 1.0), roughness=0.35)
    mat_visor = create_material("FF_Visor", (1.0, 0.08, 0.18, 1.0), roughness=0.1, emission_color=(1.0, 0.12, 0.22, 1.0), emission_strength=6.0)
    mat_gold_buckle = create_material("FF_GoldMetal", (0.95, 0.75, 0.15, 1.0), metallic=0.9, roughness=0.18)
    mat_gun_metal = create_material("FF_GunMetal", (0.08, 0.08, 0.09, 1.0), metallic=0.85, roughness=0.25)
    mat_pedestal = create_material("FF_Pedestal", (0.04, 0.04, 0.05, 1.0), metallic=0.8, roughness=0.25)

    root = bpy.data.objects.new("FreeFire_Hero", None)
    bpy.context.collection.objects.link(root)

    # ================= HEAD & FACE =================
    head = create_primitive('uv_sphere', "Head", location=(0, 0, 3.25), scale=(0.26, 0.28, 0.32), material=mat_skin, parent=root, segments=32, ring_count=20)
    chin = create_primitive('cone', "Jaw", location=(0, 0.07, 3.02), scale=(0.18, 0.20, 0.20), rotation=(math.pi, 0, 0), material=mat_skin, parent=root)
    neck = create_primitive('cylinder', "Neck", location=(0, 0.01, 2.92), scale=(0.13, 0.13, 0.16), material=mat_skin, parent=root)
    collar = create_primitive('cylinder', "TacticalCollar", location=(0, 0.01, 2.84), scale=(0.20, 0.20, 0.11), material=mat_tactical_black, parent=root)

    # Cyber Combat Visor / Tactical Goggles
    visor = create_primitive('cube', "CyberVisor", location=(0, 0.26, 3.27), scale=(0.26, 0.04, 0.06), material=mat_visor, parent=root, bevel=0.01)
    visor_frame = create_primitive('cube', "VisorFrame", location=(0, 0.24, 3.27), scale=(0.29, 0.05, 0.08), material=mat_tactical_black, parent=root, bevel=0.01)
    
    # Headset / Comm Gear
    comm_l = create_primitive('cylinder', "CommEar_L", location=(-0.27, 0.02, 3.27), scale=(0.07, 0.07, 0.04), rotation=(0, math.pi/2, 0), material=mat_ff_orange, parent=root)
    comm_r = create_primitive('cylinder', "CommEar_R", location=(0.27, 0.02, 3.27), scale=(0.07, 0.07, 0.04), rotation=(0, math.pi/2, 0), material=mat_ff_orange, parent=root)
    comm_mic = create_primitive('cylinder', "CommMic", location=(-0.22, 0.20, 3.14), scale=(0.018, 0.018, 0.14), rotation=(math.pi/2, -0.3, 0), material=mat_cyber_cyan, parent=root)

    # Anime Cyber Spiked Hair
    hair_spikes = [
        ((0.0, -0.06, 3.58), (0.2, 0, 0), (0.16, 0.24, 0.24)),
        ((-0.16, 0.02, 3.55), (0.3, -0.4, -0.2), (0.13, 0.18, 0.2)),
        ((0.16, 0.02, 3.55), (0.3, 0.4, 0.2), (0.13, 0.18, 0.2)),
        ((-0.24, -0.10, 3.44), (0.1, -0.5, -0.5), (0.12, 0.16, 0.18)),
        ((0.24, -0.10, 3.44), (0.1, 0.5, 0.5), (0.12, 0.16, 0.18)),
        ((0.0, 0.15, 3.50), (-0.45, 0, 0), (0.13, 0.2, 0.2)), # Front swept bangs
        ((-0.11, 0.19, 3.46), (-0.4, -0.2, -0.2), (0.1, 0.15, 0.18)),
        ((0.11, 0.19, 3.46), (-0.4, 0.2, 0.2), (0.1, 0.15, 0.18)),
        ((0.0, -0.21, 3.38), (0.55, 0, 0), (0.15, 0.18, 0.2)), # Back hair
    ]
    for i, (loc, rot, scl) in enumerate(hair_spikes):
        create_primitive('cone', f"HairSpike_{i}", location=loc, scale=scl, rotation=rot, material=mat_hair, parent=root)

    # ================= TORSO & TACTICAL VEST =================
    torso_inner = create_primitive('cube', "TorsoInner", location=(0, 0, 2.3), scale=(0.38, 0.23, 0.52), material=mat_tactical_black, parent=root, bevel=0.02)
    chest_plate = create_primitive('cube', "ChestPlate", location=(0, 0.08, 2.45), scale=(0.40, 0.18, 0.30), material=mat_armor, parent=root, bevel=0.03)
    back_plate = create_primitive('cube', "BackPlate", location=(0, -0.08, 2.45), scale=(0.38, 0.16, 0.30), material=mat_armor, parent=root, bevel=0.03)

    # Glowing Cyber Core & Decals on Chest
    chest_core = create_primitive('cylinder', "EnergyCore", location=(0, 0.27, 2.52), scale=(0.065, 0.065, 0.02), rotation=(math.pi/2, 0, 0), material=mat_cyber_cyan, parent=root)
    chest_stripe_l = create_primitive('cube', "ChestStripe_L", location=(-0.22, 0.265, 2.50), scale=(0.035, 0.015, 0.18), rotation=(0, 0, -0.22), material=mat_ff_orange, parent=root)
    chest_stripe_r = create_primitive('cube', "ChestStripe_R", location=(0.22, 0.265, 2.50), scale=(0.035, 0.015, 0.18), rotation=(0, 0, 0.22), material=mat_ff_orange, parent=root)

    # Tactical Ammo Pouches on lower vest
    for idx, x_pos in enumerate([-0.20, 0.0, 0.20]):
        create_primitive('cube', f"AmmoPouch_{idx}", location=(x_pos, 0.27, 2.22), scale=(0.08, 0.04, 0.09), material=mat_tactical_black, parent=root, bevel=0.01)
        create_primitive('cube', f"PouchFlap_{idx}", location=(x_pos, 0.28, 2.28), scale=(0.082, 0.035, 0.025), material=mat_armor, parent=root)
        create_primitive('cube', f"PouchLatch_{idx}", location=(x_pos, 0.315, 2.24), scale=(0.025, 0.01, 0.02), material=mat_gold_buckle, parent=root)

    # Tactical Belt & Heavy Gold Buckle
    belt = create_primitive('cube', "TacticalBelt", location=(0, 0, 1.88), scale=(0.40, 0.24, 0.07), material=mat_tactical_black, parent=root, bevel=0.01)
    belt_buckle = create_primitive('cube', "BeltBuckle", location=(0, 0.24, 1.88), scale=(0.09, 0.025, 0.06), material=mat_gold_buckle, parent=root, bevel=0.01)
    belt_core = create_primitive('cylinder', "BeltLight", location=(0, 0.268, 1.88), scale=(0.025, 0.025, 0.01), rotation=(math.pi/2, 0, 0), material=mat_cyber_cyan, parent=root)

    # ================= ARMS & HANDS =================
    # Left Arm: Holds the Tactical SMG
    # Right Arm: Lowered ready stance
    for side, sign, side_name in [(-1, -1, "L"), (1, 1, "R")]:
        # Shoulder Armor Pad
        create_primitive('uv_sphere', f"ShoulderPad_{side_name}", location=(sign * 0.48, 0.02, 2.70), scale=(0.16, 0.16, 0.16), material=mat_armor, parent=root)
        create_primitive('torus', f"ShoulderRing_{side_name}", location=(sign * 0.48, 0.02, 2.70), scale=(0.175, 0.175, 0.035), rotation=(0, 0.8 * sign, 0), material=mat_ff_orange, parent=root)

        if side_name == "L":
            # Left Arm angled forward holding weapon
            create_primitive('cylinder', f"UpperArm_{side_name}", location=(-0.50, 0.10, 2.42), scale=(0.10, 0.10, 0.20), rotation=(0.4, -0.2, 0), material=mat_skin, parent=root)
            create_primitive('cylinder', f"CyberGauntlet_{side_name}", location=(-0.46, 0.28, 2.06), scale=(0.11, 0.11, 0.22), rotation=(0.8, -0.4, 0), material=mat_tactical_black, parent=root)
            create_primitive('cube', f"GauntletDisplay_{side_name}", location=(-0.46, 0.35, 2.12), scale=(0.05, 0.02, 0.10), rotation=(0.8, -0.4, 0), material=mat_cyber_cyan, parent=root)
            create_primitive('cube', f"CombatGlove_{side_name}", location=(-0.38, 0.42, 1.82), scale=(0.08, 0.08, 0.10), rotation=(0.8, -0.4, 0), material=mat_tactical_black, parent=root)
        else:
            # Right Arm angled naturally downward
            create_primitive('cylinder', f"UpperArm_{side_name}", location=(0.50, 0.0, 2.40), scale=(0.10, 0.10, 0.20), rotation=(0, 0.12, 0), material=mat_skin, parent=root)
            create_primitive('cylinder', f"Forearm_{side_name}", location=(0.56, 0.06, 1.96), scale=(0.10, 0.10, 0.22), rotation=(0.1, 0.2, 0), material=mat_tactical_black, parent=root)
            create_primitive('cylinder', f"ArmBand_{side_name}", location=(0.55, 0.05, 2.10), scale=(0.115, 0.115, 0.035), rotation=(0.1, 0.2, 0), material=mat_ff_orange, parent=root)
            create_primitive('cube', f"CombatGlove_{side_name}", location=(0.62, 0.10, 1.62), scale=(0.08, 0.08, 0.11), rotation=(0.1, 0.2, 0), material=mat_tactical_black, parent=root)
            create_primitive('cube', f"KnuckleGuard_{side_name}", location=(0.62, 0.15, 1.60), scale=(0.07, 0.02, 0.04), rotation=(0.1, 0.2, 0), material=mat_armor, parent=root)

    # ================= WEAPON: FUTURISTIC TACTICAL SMG (Held by Left Hand) =================
    gun_root = bpy.data.objects.new("TacticalSMG", None)
    gun_root.parent = root
    gun_root.location = (-0.32, 0.44, 1.85)
    gun_root.rotation_euler = (math.radians(-25), math.radians(10), math.radians(-35))
    bpy.context.collection.objects.link(gun_root)

    # Gun Body
    create_primitive('cube', "SMG_Receiver", location=(0, 0, 0), scale=(0.05, 0.35, 0.10), material=mat_gun_metal, parent=gun_root, bevel=0.01)
    create_primitive('cube', "SMG_Barrel", location=(0, 0.38, 0.02), scale=(0.035, 0.18, 0.035), material=mat_tactical_black, parent=gun_root)
    create_primitive('cylinder', "SMG_Muzzle", location=(0, 0.58, 0.02), scale=(0.04, 0.04, 0.06), rotation=(math.pi/2, 0, 0), material=mat_ff_orange, parent=gun_root)
    # Magazine
    create_primitive('cube', "SMG_Magazine", location=(0, 0.08, -0.16), scale=(0.04, 0.08, 0.14), rotation=(0.25, 0, 0), material=mat_tactical_black, parent=gun_root)
    # Gun Grip
    create_primitive('cube', "SMG_Grip", location=(0, -0.14, -0.12), scale=(0.04, 0.06, 0.12), rotation=(-0.35, 0, 0), material=mat_tactical_black, parent=gun_root)
    # Cyber Scope with glowing reticle
    create_primitive('cube', "SMG_Scope", location=(0, 0.02, 0.12), scale=(0.04, 0.18, 0.05), material=mat_gun_metal, parent=gun_root, bevel=0.01)
    create_primitive('cylinder', "SMG_ScopeLens", location=(0, 0.20, 0.12), scale=(0.03, 0.03, 0.01), rotation=(math.pi/2, 0, 0), material=mat_cyber_cyan, parent=gun_root)
    # Neon Accent Line on Gun
    create_primitive('cube', "SMG_NeonDecal", location=(0.051, 0.05, 0.0), scale=(0.005, 0.22, 0.02), material=mat_ff_orange, parent=gun_root)

    # ================= LEGS & CARGO PANTS =================
    pelvis = create_primitive('cube', "Pelvis", location=(0, 0, 1.70), scale=(0.35, 0.22, 0.15), material=mat_pants, parent=root, bevel=0.02)

    for side, sign, side_name in [(-1, -1, "L"), (1, 1, "R")]:
        thigh_rot = Euler((0.04, sign * 0.07, 0), 'XYZ')
        create_primitive('cylinder', f"Thigh_{side_name}", location=(sign * 0.22, 0.02, 1.35), scale=(0.15, 0.15, 0.27), rotation=thigh_rot, material=mat_pants, parent=root)

        if side_name == "R":
            create_primitive('cube', f"ThighHolster_{side_name}", location=(sign * 0.38, 0.04, 1.35), scale=(0.06, 0.11, 0.15), material=mat_tactical_black, parent=root, bevel=0.01)
            create_primitive('torus', f"HolsterStrap1_{side_name}", location=(sign * 0.22, 0.02, 1.45), scale=(0.17, 0.17, 0.025), material=mat_ff_orange, parent=root)
            create_primitive('torus', f"HolsterStrap2_{side_name}", location=(sign * 0.22, 0.02, 1.25), scale=(0.17, 0.17, 0.025), material=mat_ff_orange, parent=root)
        else:
            create_primitive('cube', f"CargoPouch_{side_name}", location=(sign * 0.36, 0.02, 1.35), scale=(0.05, 0.13, 0.13), material=mat_tactical_black, parent=root, bevel=0.01)
            create_primitive('cube', f"CargoFlap_{side_name}", location=(sign * 0.37, 0.02, 1.44), scale=(0.055, 0.13, 0.03), material=mat_armor, parent=root)

        # Knee Pad
        create_primitive('uv_sphere', f"Knee_{side_name}", location=(sign * 0.24, 0.13, 0.96), scale=(0.11, 0.10, 0.13), material=mat_armor, parent=root)
        create_primitive('cube', f"KneeGuard_{side_name}", location=(sign * 0.24, 0.22, 0.96), scale=(0.09, 0.025, 0.11), material=mat_tactical_black, parent=root, bevel=0.01)
        create_primitive('cube', f"KneeStripe_{side_name}", location=(sign * 0.24, 0.248, 0.96), scale=(0.07, 0.01, 0.025), material=mat_cyber_cyan, parent=root)

        # Shin / Calf
        create_primitive('cylinder', f"Shin_{side_name}", location=(sign * 0.24, 0.02, 0.62), scale=(0.13, 0.13, 0.24), material=mat_pants, parent=root)

        # Combat Boots
        create_primitive('cylinder', f"BootUpper_{side_name}", location=(sign * 0.24, 0.02, 0.38), scale=(0.15, 0.15, 0.17), material=mat_tactical_black, parent=root)
        create_primitive('cube', f"BootFoot_{side_name}", location=(sign * 0.24, 0.10, 0.14), scale=(0.14, 0.28, 0.11), material=mat_tactical_black, parent=root, bevel=0.02)
        create_primitive('cube', f"BootSole_{side_name}", location=(sign * 0.24, 0.10, 0.04), scale=(0.15, 0.30, 0.04), material=mat_ff_orange, parent=root, bevel=0.01)
        create_primitive('uv_sphere', f"BootToe_{side_name}", location=(sign * 0.24, 0.28, 0.10), scale=(0.13, 0.11, 0.08), material=mat_armor, parent=root)
        create_primitive('cube', f"BootGlow_{side_name}", location=(sign * 0.24, -0.15, 0.06), scale=(0.11, 0.02, 0.035), material=mat_cyber_cyan, parent=root)

    # ================= BACK GEAR: CYBER BLADE / KATANA =================
    blade_root = bpy.data.objects.new("CyberBlade", None)
    blade_root.parent = root
    blade_root.location = (0.10, -0.25, 2.38)
    blade_root.rotation_euler = (0.42, 0.28, -0.42)
    bpy.context.collection.objects.link(blade_root)

    create_primitive('cube', "BladeSheath", location=(0, 0, 0), scale=(0.035, 0.07, 0.85), material=mat_tactical_black, parent=blade_root, bevel=0.01)
    create_primitive('cube', "SheathTrim1", location=(0, 0, 0.38), scale=(0.04, 0.075, 0.035), material=mat_ff_orange, parent=blade_root)
    create_primitive('cube', "SheathTrim2", location=(0, 0, -0.38), scale=(0.04, 0.075, 0.035), material=mat_ff_orange, parent=blade_root)
    create_primitive('cube', "SheathEnergyLine", location=(0.036, 0, 0), scale=(0.01, 0.015, 0.65), material=mat_cyber_cyan, parent=blade_root)
    create_primitive('cube', "BladeGuard", location=(0, 0, 0.90), scale=(0.05, 0.13, 0.025), material=mat_gold_buckle, parent=blade_root)
    create_primitive('cylinder', "BladeHilt", location=(0, 0, 1.08), scale=(0.025, 0.025, 0.17), material=mat_tactical_black, parent=blade_root)
    create_primitive('uv_sphere', "BladePommel", location=(0, 0, 1.27), scale=(0.035, 0.035, 0.035), material=mat_gold_buckle, parent=blade_root)

    # ================= SHOWROOM CYBER PEDESTAL =================
    create_primitive('cylinder', "CyberPodium", location=(0, 0, -0.06), scale=(1.8, 1.8, 0.06), material=mat_pedestal, vertices=32)
    create_primitive('cylinder', "CyberPodiumInner", location=(0, 0, -0.01), scale=(1.5, 1.5, 0.04), material=mat_tactical_black, vertices=32)
    create_primitive('torus', "PodiumNeonRing", location=(0, 0, 0.025), scale=(1.52, 1.52, 0.025), material=mat_cyber_cyan)
    create_primitive('torus', "PodiumOrangeRing", location=(0, 0, -0.02), scale=(1.82, 1.82, 0.02), material=mat_ff_orange)

    # ================= LIGHTING SETUP =================
    # Key Light (Front-Right Warm Hero Light)
    key_light_data = bpy.data.lights.new(name="FF_KeyLight", type='AREA')
    key_light_data.energy = 1100
    key_light_data.color = (1.0, 0.94, 0.88)
    key_light_data.size = 2.5
    key_light = bpy.data.objects.new("FF_KeyLight", key_light_data)
    key_light.location = (2.2, 3.2, 3.8)
    bpy.context.collection.objects.link(key_light)

    # Rim Light (Back-Left Neon Cyan Light for iconic gamer edge-glow)
    rim_light_data = bpy.data.lights.new(name="FF_RimLight_Cyan", type='AREA')
    rim_light_data.energy = 1500
    rim_light_data.color = (0.0, 0.85, 1.0)
    rim_light_data.size = 3.0
    rim_light = bpy.data.objects.new("FF_RimLight_Cyan", rim_light_data)
    rim_light.location = (-2.6, -2.4, 3.2)
    bpy.context.collection.objects.link(rim_light)

    # Front-Left Orange Fill Light
    fill_light_data = bpy.data.lights.new(name="FF_FillLight_Orange", type='POINT')
    fill_light_data.energy = 600
    fill_light_data.color = (1.0, 0.40, 0.05)
    fill_light = bpy.data.objects.new("FF_FillLight_Orange", fill_light_data)
    fill_light.location = (-2.4, 2.2, 2.0)
    bpy.context.collection.objects.link(fill_light)

    # Under-glow Light for Podium
    under_light_data = bpy.data.lights.new(name="FF_PodiumGlow", type='POINT')
    under_light_data.energy = 350
    under_light_data.color = (0.0, 0.9, 1.0)
    under_light = bpy.data.objects.new("FF_PodiumGlow", under_light_data)
    under_light.location = (0, 0, 0.35)
    bpy.context.collection.objects.link(under_light)

    # Focus target at character torso/belt
    focus_target = bpy.data.objects.new("CameraFocusTarget", None)
    focus_target.location = (0, 0, 1.7)
    bpy.context.collection.objects.link(focus_target)

    # Aim lights
    for lt in [key_light, rim_light]:
        tt = lt.constraints.new(type='TRACK_TO')
        tt.target = focus_target
        tt.track_axis = 'TRACK_NEGATIVE_Z'
        tt.up_axis = 'UP_Y'

    # ================= HERO CAMERA SETUP (Full Body Hero Stance) =================
    cam_data = bpy.data.cameras.new(name="HeroCamera")
    cam_data.lens = 38  # Wide heroic showcase
    cam_data.clip_start = 0.1
    cam_data.clip_end = 100
    cam = bpy.data.objects.new("HeroCamera", cam_data)
    # Positioned at dynamic front 3/4 heroic angle
    cam.location = (1.6, 6.4, 1.6)
    
    # Track to character
    cam_track = cam.constraints.new(type='TRACK_TO')
    cam_track.target = focus_target
    cam_track.track_axis = 'TRACK_NEGATIVE_Z'
    cam_track.up_axis = 'UP_Y'
    
    bpy.context.collection.objects.link(cam)
    bpy.context.scene.camera = cam

    # Set background world
    world = bpy.data.worlds.new("FreeFire_World")
    world.node_tree.nodes["Background"].inputs["Color"].default_value = (0.015, 0.018, 0.025, 1.0)
    world.node_tree.nodes["Background"].inputs["Strength"].default_value = 0.5
    bpy.context.scene.world = world

    # Render settings
    bpy.context.scene.render.resolution_x = 1920
    bpy.context.scene.render.resolution_y = 1080

    print("Free Fire Stylized Character Model generated successfully!")

if __name__ == "__main__":
    build_freefire_character()
