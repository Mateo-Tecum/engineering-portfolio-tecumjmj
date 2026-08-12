function [Bz, Br] = solenoid_field_map(rVec, zVec, Rcoil, Lcoil, N, I)
%SOLENOID_FIELD_MAp Approximate 2d axisymmetric field of a finite solenoid.
%
% The solenoid is discretized into circular current loops. Each loop is
% further discretized into straight current elements and evaluated using
% the Biot-Savart law.
%
% This implementation prioritizes readability over maximum performance.

mu0 = 4*pi*1e-7;

% Number of loops used numerically. A larger value gives smoother results
nLoops = min(N,120);
loopZ = linspace(-Lcoil/2,Lcoil/2,nLoops);

% Each numerical loop represents this many physical turns
turnWeight = N/nLoops;

% Angular discretization around each circular loop
nPhi = 160;
phi = linspace(0,2*pi,nPhi+1);
phi(end) = [];
dphi = 2*pi/nPhi;

[Rgrid,Zgrid] = meshgrid(rVec,zVec);
Bz = zeros(size(Rgrid));
Br = zeros(size(Rgrid));

% Evaluate field in x-z plane; by symmetry x corresponds to radial direction
for k = 1:nLoops
    zk = loopZ(k);

    for m = 1:nPhi
        ph = phi(m);

        % Source point on current loop
        xs = Rcoil*cos(ph);
        ys = Rcoil*sin(ph);
        zs = zk;

        % Differential current element dl
        dlx = -Rcoil*sin(ph)*dphi;
        dly =  Rcoil*cos(ph)*dphi;
        dlz = 0;

        % Observation points are at (r,0,z)
        rx = Rgrid-xs;
        ry = -ys;
        rz = Zgrid-zs;

        r3 = (rx.^2+ry.^2+rz.^2).^(3/2);
        r3(r3==0) = eps;

        % dl cross r
        cx = dly.*rz - dlz.*ry;
        cy = dlz.*rx - dlx.*rz;
        cz = dlx.*ry - dly.*rx;

        factor = (mu0/(4*pi))*I*turnWeight;

        % x-direction is radial in selected meridional plane
        Br = Br + factor*cx./r3;
        Bz = Bz + factor*cz./r3;
    end
end
end
